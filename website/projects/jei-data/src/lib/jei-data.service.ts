import { ErrorHandler, Injectable } from '@angular/core';
import { JeiData, JeiDataSchema } from './data';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { DetailedError } from './error-utils';

@Injectable({
    providedIn: 'root',
})
export class JeiDataService {

    private static_file_url: string | null = null
    private user_file: File | null = null
    private data_cache: JeiData | null = null

    constructor(private http: HttpClient) { }

    public select_static_file(static_file_url: string) {
        this.static_file_url = static_file_url
        this.user_file = null
        this.data_cache = null
    }

    public select_user_file(user_file: File) {
        this.static_file_url = null
        this.user_file = user_file
        this.data_cache = null
    }

    public unselect_file() {
        this.static_file_url = null
        this.user_file = null
        this.data_cache = null
    }

    private async fetch_static_file(static_file_url: string): Promise<Object> {
        try {
            const data = await firstValueFrom(
                this.http.get(static_file_url, { responseType: 'json' })
            )

            return data
        } catch (err) {
            throw new DetailedError(`Error fetching/parsing static JSON file: ${static_file_url}`, {err})
        }
    }

    private async fetch_user_file(user_file: File): Promise<Object> {
        const content_promise = new Promise<string>((resolve, reject) => {
            const reader = new FileReader();

            reader.onload = () => resolve(reader.result as string);
            reader.onerror = () => reject(reader.error);

            reader.readAsText(user_file);
        });

        let content

        try {
            content = await content_promise;
        } catch (err) {
            throw new DetailedError('Fetching user file content failed', {err})
        }

        try {
            return JSON.parse(content)
        } catch (err) {
            throw new DetailedError('Parsing user file JSON structure failed', {err, content})
        }
    }

    public async get_data(): Promise<JeiData> {
        if (this.data_cache != null) {
            return this.data_cache
        }

        let json: Object

        if (this.static_file_url != null) {
            json = await this.fetch_static_file(this.static_file_url)
        } else if (this.user_file != null) {
            json = await this.fetch_user_file(this.user_file)
        } else {
            throw new Error('No file selected')
        }

        try {
            this.data_cache = JeiDataSchema.parse(json)

            return this.data_cache
        } catch (err) {
            throw new DetailedError('JSON does not resemble JeiData structure', {err})
        }
    }
}

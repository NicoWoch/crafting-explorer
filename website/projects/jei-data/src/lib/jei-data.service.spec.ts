import { TestBed } from '@angular/core/testing';
import { JeiDataService } from './jei-data.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import test_valid_json from './test-valid.json'
import { DetailedError } from './error-utils';
import { JeiData } from './data';

describe('JeiDataService', () => {
    let service: JeiDataService
    let httpMock: HttpTestingController

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [JeiDataService]
        });

        service = TestBed.inject(JeiDataService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should throw if no file is selected', async () => {
        service.unselect_file()

        await expectAsync(service.get_data()).toBeRejectedWithError(Error, 'No file selected')
    })

    it('should parse static file test-valid.json', async () => {
        service.select_static_file('test-valid.json')

        const data_promise = service.get_data()

        const req = httpMock.expectOne('test-valid.json');
        expect(req.request.method).toBe('GET');
        req.flush(test_valid_json);

        expect(await data_promise).toBeTruthy()

        console.log(await service.get_data())
    })

    it('should not parse invalid JeiData structure', async () => {
        service.select_static_file('invalid.json')

        const data_promise = service.get_data()

        const req = httpMock.expectOne('invalid.json');
        expect(req.request.method).toBe('GET');
        req.flush({adam: 1, items: []});

        await expectAsync(data_promise).toBeRejectedWithError(DetailedError, 'JSON does not resemble JeiData structure')
    })

    it('should have correct data when parsing test-valid.json', async () => {
        service.select_static_file('test-valid.json')

        const data_promise = service.get_data()

        const req = httpMock.expectOne('test-valid.json');
        expect(req.request.method).toBe('GET');
        req.flush(test_valid_json);

        const data = await data_promise

        expect(data.items.length).toBe(4171)
        expect(data.recipes.length).toBe(3045)
        expect(data.recipes_categories.length).toBe(13)
        expect(data.slots.length).toBe(3104)

        expect(data.items[0].display_name).toBe('Paper')
        expect(data.items[0].id).toBe('minecraft:paper')
        expect(data.items[0].metadata).toBe(0)

        expect(data.items[0].specific_data).toEqual({
            base_uid: "minecraft__paper__0",
            item_uid: "minecraft__paper__0",
            damageable: "false",
            has_subtypes: "false",
            nbt: "{}"
        })
    })

    it('should invalidate JeiData cache', async () => {
        const obj1: JeiData = {
            items: [],
            recipes: [],
            recipes_categories: [],
            slots: [[[1, 2]], []],
        }

        const obj2: JeiData = {
            items: [],
            recipes: [],
            recipes_categories: [
                {uid: 'uid', mod_name: 'mod_name', display_name: 'display_name'}
            ],
            slots: [[]],
        }

        const obj3: JeiData = {
            items: [],
            recipes: [],
            recipes_categories: [],
            slots: [[[7, 1]], [[4, 5]]],
        }

        const test_obj = async (url: string, obj: Object) => {
            const data_promise = service.get_data()

            const req = httpMock.expectOne(url)
            expect(req.request.method).toBe('GET')
            req.flush(obj)

            expect(await data_promise).toBeTruthy()
        }

        expect((service as any).data_cache).toBeNull()

        service.select_static_file('file1.json')
        await test_obj('file1.json' ,obj1)

        expect((service as any).data_cache).toBeTruthy()
        
        service.select_static_file('file2.json')

        expect((service as any).data_cache).toBeNull()

        await test_obj('file2.json', obj2)

        expect((service as any).data_cache).toBeTruthy()

        service.select_static_file('file3.json')
        await test_obj('file3.json', obj3)
    })
})

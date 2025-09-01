import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-file-prompt',
    imports: [FormsModule],
    templateUrl: './file-prompt.component.html',
    styleUrl: './file-prompt.component.scss',
})
export class FilePromptComponent {
    @Input() public allowedExtensions!: string

    @Output() public fileSelected: EventEmitter<File> = new EventEmitter()

    protected onFileSelected(event: Event) {
        const elem = event.target as HTMLInputElement

        if ((elem.files?.length ?? 0) == 0) {
            return
        }

        const file = elem.files?.item(0)

        if (file == null || file == undefined) { 
            return
        }

        this.fileSelected.emit(file)
    }
}

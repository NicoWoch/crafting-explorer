import { Pipe, PipeTransform, Renderer2 } from '@angular/core';
import { ItemInfo } from '../item/item.component';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

const COLORS_MAP: Map<string, string> = new Map<string, string>([
    ['0', '#ffffff'],  // changed black to white, cause black is invisible in ui
    ['1', '#0000AA'],
    ['2', '#00AA00'],
    ['3', '#00AAAA'],
    ['4', '#AA0000'],
    ['5', '#AA00AA'],
    ['6', '#FFAA00'],
    ['7', '#AAAAAA'],
    ['8', '#555555'],
    ['9', '#5555FF'],
    ['a', '#55FF55'],
    ['b', '#55FFFF'],
    ['c', '#FF5555'],
    ['d', '#FF55FF'],
    ['e', '#FFFF55'],
    ['f', '#FFFFFF'],
    ['g', '#DDD605'],
    ['h', '#E3D4D1'],
    ['i', '#CECACA'],
    ['j', '#443A3B'],
    ['m', '#971607'],
    ['n', '#B4684D'],
    ['p', '#DEB12D'],
    ['q', '#47A036'],
    ['s', '#2CBAA8'],
    ['t', '#21497B'],
    ['u', '#9A5CC6'],
    ['r', '#ffffff'],
])

@Pipe({
    name: 'minecraftColors',
    pure: true
})
export class MinecraftColorsPipe implements PipeTransform {
    constructor(private renderer: Renderer2, private sanitizer: DomSanitizer) {}
    
    transform(message: string): SafeHtml {
        const master_span = this.renderer.createElement('span')

        let current_content = ''
        let current_color = '#ffffff'
        let paragraph_code = false

        for (const char of message) {
            if (char === '�') {
                paragraph_code = true
                continue
            }

            if (paragraph_code) {
                const current_child = this.renderer.createElement('span')
                this.renderer.setStyle(current_child, 'color', current_color)
                this.renderer.setProperty(current_child, 'textContent', current_content)
                this.renderer.appendChild(master_span, current_child)

                current_content = ''
                current_color = COLORS_MAP.get(char) ?? '#ffffff'
                paragraph_code = false
                continue
            }

            current_content += char
        }

        const current_child = this.renderer.createElement('span')
        this.renderer.setStyle(current_child, 'color', current_color)
        this.renderer.setProperty(current_child, 'textContent', current_content)
        this.renderer.appendChild(master_span, current_child)

        return this.sanitizer.bypassSecurityTrustHtml(master_span.innerHTML)
    }
}
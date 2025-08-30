import { Component, Input } from '@angular/core';
import { Item } from 'jei-data';
import { RecipesViewService } from '../recipes-view/recipes-view.service';
import { MinecraftColorsPipe } from "./minecraft-colors.pipe";

export interface ItemInfo {
    item_index: number
    item: Item
    count: number
}

@Component({
    selector: 'app-item',
    templateUrl: './item.component.html',
    styleUrl: './item.component.scss',
    imports: [MinecraftColorsPipe]
})
export class ItemComponent {
    @Input({required: true}) items: ItemInfo[] = []

    @Input() switch_interval: number = 2000

    protected current_icon_src: string = ''
    protected current_index: number = 0

    constructor(private recipes_view_service: RecipesViewService) {}

    protected ngOnInit() {
        setInterval(() => this.nextItem(), this.switch_interval)
    }

    protected ngOnChanges() {
        this.current_index = -1
        this.nextItem()
    }

    protected get current_item(): ItemInfo | undefined {
        return this.current_index < this.items.length ? this.items[this.current_index] : undefined
    }

    protected get current_item_details(): [string, string][] {
        if (this.current_item === undefined) return []
        
        const details: [string, string][] = []

        details.push(['ID', this.current_item.item.id])
        details.push(['Metadata', this.current_item.item.metadata.toString()])
        details.push(['DB Index', this.current_item.item_index.toString()])
        details.push(...Object.entries(this.current_item.item.specific_data))

        return details
    }

    public nextItem() {
        if (this.items.length === 0) {
            return
        }

        this.current_index = (this.current_index + 1) % this.items.length

        this.setCurrentIcon(this.current_item?.item.icon_base64)
    }

    private setCurrentIcon(icon_base64?: string) {
        let src

        if (icon_base64 === undefined) {
            src = 'assets/icon_not_found.png'
        } else {
            src = `data:image/png;base64,${icon_base64}`
        }

        if (this.failed_icons.has(src)) {
            src = 'assets/icon_not_found.png'
        }

        this.current_icon_src = src
    }

    protected getCurrentItemDisplayName(): string {
        if (this.current_item !== undefined) {
            return this.current_item.item.display_name
        }
        
        return "<no item>"
    }

    protected getCurrentItemDisplayNameDetails(): string {
        if (this.current_item !== undefined) {
            const display_metadata = this.current_item.item.metadata != 0

            const display_nbt = this.current_item.item.specific_data["nbt"] !== undefined &&
                                this.current_item.item.specific_data["nbt"] !== "{}"

            return (display_metadata ? "  @" + this.current_item.item.metadata : "") +
                    (display_nbt ? "  (+NBT)" : "")
        }

        return ""
    }

    private failed_icons: Set<string> = new Set()

    protected onIconError(event: Event) {
        const img = event.target as HTMLImageElement;
        
        // this.failed_icons.add(img.src)

        // img.src = '/assets/icon_not_found.png';
    }

    protected onLeftClick(event: Event) {
        if (this.current_item !== undefined) {
            this.recipes_view_service.showHowToMake(this.current_item.item_index)
            event.preventDefault()
        }
    }

    protected onRightClick(event: Event) {
        if (this.current_item !== undefined) {
            this.recipes_view_service.showWhatCanBeMade(this.current_item.item_index)
            event.preventDefault()
        }
    }
}

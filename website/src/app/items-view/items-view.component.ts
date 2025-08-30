import { Component, Input } from '@angular/core';
import { ItemComponent, ItemInfo } from "../item/item.component";
import { ItemsFilterPipe } from './items-filter.pipe';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-items-view',
    imports: [ItemComponent, ItemsFilterPipe, FormsModule],
    templateUrl: './items-view.component.html',
    styleUrl: './items-view.component.scss'
})
export class ItemsViewComponent {
    @Input() public items: ItemInfo[] = []
    @Input() public switch_interval: number = 2000 * 100000

    protected current_page: number = 0
    protected items_per_page: number = 200

    protected query: string = '';

    public get count_of_pages(): number {
        return Math.ceil(this.items.length / this.items_per_page)
    }

    public nextPage() {
        if (this.count_of_pages === 0) {
            this.current_page = 0
            return
        }

        this.current_page = (this.current_page + 1 + this.count_of_pages) % this.count_of_pages
    }

    public previousPage() {
        if (this.count_of_pages === 0) {
            this.current_page = 0
            return
        }
        
        this.current_page = (this.current_page - 1 + this.count_of_pages) % this.count_of_pages
    }
}

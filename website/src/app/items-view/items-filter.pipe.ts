import { Pipe, PipeTransform } from '@angular/core';
import { ItemInfo } from '../item/item.component';

@Pipe({
    name: 'itemsFilter',
    pure: true // pure pipe, recalculates only when inputs change
})
export class ItemsFilterPipe implements PipeTransform {
    transform(items: ItemInfo[], current_page: number, items_per_page: number, query: string): ItemInfo[] {
        query = query.toLowerCase();

        const range = [current_page * items_per_page, (current_page + 1) * items_per_page]

        return items.filter(item_info => {
            const display_name = item_info.item.display_name.toLowerCase()
            const id = item_info.item.id.toLowerCase()

            return (display_name + id).includes(query)
        }).filter((_, index) => index >= range[0] && index < range[1]);
    }
}
import { Component } from '@angular/core';
import { ItemComponent, ItemInfo } from "./item/item.component";
import { JeiDataService, Item } from 'jei-data';
import { ItemsViewComponent } from "./items-view/items-view.component";
import { RecipeInfo, RecipesMap, RecipesViewComponent } from "./recipes-view/recipes-view.component";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss',
    imports: [ItemsViewComponent, RecipesViewComponent],
})
export class AppComponent {
    protected items: ItemInfo[] = []
    protected recipes: RecipesMap | null = null

    constructor(private jei_service: JeiDataService) {}

    protected ngOnInit() {
        console.debug('Loading default data...')
        this.jei_service.select_static_file('assets/e2e_new_export.json')

        this.jei_service.get_data().then(data => {
            console.debug('Loaded default data')
            console.debug(data)

            this.items = data.items.map((item, i) => ({
                item_index: i,
                item,
                count: 1,
            })).sort((a, b) => {
                const plus_one = '+-()[]{}*<>.%/\"'.includes(a.item.display_name[0])
                const minus_one = '+-()[]{}*<>.%/\"'.includes(b.item.display_name[0])

                if (plus_one && !minus_one) {
                    return 1
                } else if (minus_one && !plus_one) {
                    return -1
                }

                return a.item.display_name.localeCompare(b.item.display_name)
            })
        })
    }
}

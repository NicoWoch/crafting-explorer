import { Component } from '@angular/core';
import { ItemComponent, ItemInfo } from "./item/item.component";
import { JeiDataService, Item } from 'jei-data';
import { ItemsViewComponent } from "./items-view/items-view.component";
import { RecipeInfo, RecipesMap, RecipesViewComponent } from "./recipes-view/recipes-view.component";
import { FilePromptComponent } from "./file-prompt/file-prompt.component";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss',
    imports: [ItemsViewComponent, RecipesViewComponent, FilePromptComponent],
})
export class AppComponent {
    protected items: ItemInfo[] = []
    protected recipes: RecipesMap | null = null

    protected selected_file: File | null = null

    protected error_message: string = ''
    protected is_explorer_opened: boolean = false

    constructor(private jei_service: JeiDataService) {}

    protected open_selected_file() {
        if (this.selected_file === null) {
            this.error_message = 'Please select a file'
            return
        }

        console.debug('Loading data...')
        this.jei_service.select_user_file(this.selected_file)

        this.jei_service.get_data().then(data => {
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

            this.error_message = ''
            console.debug('Loaded data')
            this.is_explorer_opened = true
        }).catch(error => {
            this.error_message = `Something went wrong\n${error}`
        })
    }

    protected close_file() {
        this.jei_service.unselect_file()
        this.is_explorer_opened = false
    }
}

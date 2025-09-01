import { Component, Input } from '@angular/core';
import { ItemComponent, ItemInfo } from "../item/item.component";
import { RecipesViewService } from './recipes-view.service';
import { DefaultMap } from './default-map';

export interface RecipeInfo {
    inputs: ItemInfo[][]
    outputs: ItemInfo[][]
}

class CategorySpecific {
    constructor(
        public inputs_grid_columns?: number,
        public outputs_grid_columns?: number
    ) {}

    public getInputGridClass(): string {
        return this.inputs_grid_columns ? ` grid-cols-${this.inputs_grid_columns}` : ''
    }

    public getOutputGridClass(): string {
        return this.outputs_grid_columns ? ` grid-cols-${this.outputs_grid_columns}` : ''
    }
}

export type RecipesMap = Map<String, RecipeInfo[]>;

@Component({
    selector: 'app-recipes-view',
    templateUrl: './recipes-view.component.html',
    styleUrl: './recipes-view.component.scss',
    imports: [ItemComponent],
})
export class RecipesViewComponent {
    @Input({required: true}) public recipes: RecipesMap | null = null

    protected activeCategory: string = ''

    protected categories_specifics: DefaultMap<String, CategorySpecific> = new DefaultMap<String, CategorySpecific>(
        () => new CategorySpecific(),
        [
            ['Crafting', new CategorySpecific(3, 1)],
        ]
    )

    constructor(private recipes_view_service: RecipesViewService) {}

    protected ngOnInit() {
        this.recipes_view_service.subscribe(recipes => {
            console.debug(recipes)
            
            this.recipes = recipes
            this.activeCategory = this.recipes?.keys().next().value as string ?? 'Crafting'
        })
    }

    protected getGoBackSteps(): number {
        return this.recipes_view_service.numberOfSteps
    }

    protected goBack() {
        this.recipes_view_service.goToPreviousStep()
    }

    protected reset() {
        this.recipes_view_service.resetSteps()
    }
}

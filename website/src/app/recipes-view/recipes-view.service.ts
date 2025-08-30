import { Injectable } from "@angular/core";
import { Item, JeiDataService, Recipe } from "jei-data";
import { RecipeInfo, RecipesMap } from "./recipes-view.component";
import { JeiData } from "../../../projects/jei-data/src/public-api";
import { ItemInfo } from "../item/item.component";

@Injectable({
    providedIn: 'root'
})
export class RecipesViewService {
    private steps: RecipesMap[] = []

    constructor(private jei_service: JeiDataService) {}

    public async showHowToMake(item_index: number) {
        const jei_data = await this.jei_service.get_data()

        const recipes = jei_data.recipes.filter(
            recipe => recipe.outputSlots.some(
                slot_index => jei_data.slots[slot_index].some(slot_item => slot_item[0] == item_index)
            )
        )

        this.addNewStepFromRecipes(recipes, jei_data)
    }

    public async showWhatCanBeMade(item_index: number) {
        const jei_data = await this.jei_service.get_data()

        const recipes = jei_data.recipes.filter(
            recipe => recipe.inputSlots.some(
                slot_index => jei_data.slots[slot_index].some(slot_item => slot_item[0] == item_index)
            )
        )

        this.addNewStepFromRecipes(recipes, jei_data)
    }

    private addNewStepFromRecipes(recipes: Recipe[], jei_data: JeiData) {
        const categories = [...new Set(recipes.map(recipe => jei_data.recipes_categories.find(cat => cat.uid === recipe.category_uid)))]

        const step: RecipesMap = new Map<string, RecipeInfo[]>(categories.map(category => [
            category?.display_name ?? '<Unknown>',
            recipes.filter(recipe => recipe.category_uid === category?.uid).map(recipe => ({
                inputs: this.parseSlotsToItemInfo(recipe.inputSlots, jei_data),
                outputs: this.parseSlotsToItemInfo(recipe.outputSlots, jei_data),
            }))
        ]))

        this.steps.push(step)
        this.updateNewestRecipes()
    }

    private parseSlotsToItemInfo(slots: number[], jei_data: JeiData): ItemInfo[][] {
        return slots.map(slot => jei_data.slots[slot].map(slot_info => this.parseSlotInfoToItemInfo(slot_info, jei_data)))
    }

    private parseSlotInfoToItemInfo(slot_info: [number, number], jei_data: JeiData): ItemInfo {
        const item_index = slot_info[0]

        return {
            item_index,
            item: jei_data.items[item_index],
            count: slot_info[1],
        }
    }

    public goToPreviousStep() {
        this.steps.pop()
        this.updateNewestRecipes()
    }

    public resetSteps() {
        this.steps = []
        this.updateNewestRecipes()
    }

    public get numberOfSteps(): number {
        return this.steps.length
    }

    // Observer pattern part

    private handlers: ((recipes: RecipesMap | null) => any)[] = []

    public subscribe(handler: (recipes: RecipesMap | null) => any) {
        this.handlers.push(handler)
    }

    private updateNewestRecipes() {
        const data = this.steps.length === 0 ? null : this.steps[this.steps.length - 1]

        this.handlers.forEach(handler => handler(data))
    }
}
import * as z from "zod";
 

const RecipeSchema = z.strictObject({
    category_uid: z.string(),
    type: z.string(),

    inputSlots: z.array(z.int()),
    outputSlots: z.array(z.int()),
})

const CategorySchema = z.strictObject({
    uid: z.string(),
    mod_name: z.string(),
    display_name: z.string(),
})

const ItemSchema = z.strictObject({
    type: z.string(),
    id: z.string(),
    display_name: z.string(),
    metadata: z.int(),
    specific_data: z.record(z.string(), z.string()),
    icon_index: z.int(),
    icon_base64: z.optional(z.string()),
})

const IngredientSchema = z.tuple([z.int(), z.int()])

export const JeiDataSchema = z.strictObject({
    items: z.array(ItemSchema),
    recipes: z.array(RecipeSchema),
    recipes_categories: z.array(CategorySchema),
    slots: z.array(z.array(IngredientSchema)),
    icons_base64: z.array(z.string()),
}).transform(data => {
    data.items.forEach(item => {
        item.icon_base64 = data.icons_base64[item.icon_index]
    })
    
    return data
})


export type Recipe = z.infer<typeof RecipeSchema>
export type Category = z.infer<typeof CategorySchema>
export type Item = z.infer<typeof ItemSchema>
export type Ingredient = z.infer<typeof IngredientSchema>
export type JeiData = z.infer<typeof JeiDataSchema>

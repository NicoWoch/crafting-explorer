/*
 * Public API Surface of jei-data
 */

export { JeiDataService } from './lib/jei-data.service';

export type {
    Recipe,
    Category,
    Item,
    Ingredient,
    JeiData
} from './lib/data';

export { DetailedError } from './lib/error-utils';

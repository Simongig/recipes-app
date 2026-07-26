// One-off migration: run with mongosh against the target database.
//
// Background: Ingredient.name used to be annotated @BsonId, so every embedded
// ingredient in Recipe.ingredients[] is stored under the BSON key "_id" instead
// of "name". That annotation has been removed (ingredient identity/popularity
// tracking via a separate IngredientName collection has been dropped entirely
// in favor of deriving the autocomplete list live from Recipe.ingredients).
//
// Ingredient.unit also changed from a free-text String to the Unit enum
// (model/Unit.java), whose JSON/BSON representation is the enum constant name
// (e.g. "GRAM"), not the German display label ("Gramm") that used to be stored.
// This script maps the known legacy German labels to their enum names. Any
// stored unit value that isn't one of the known legacy labels or an
// already-migrated enum name is left untouched and reported at the end for
// manual review - the app will fail to deserialize that recipe's ingredient
// until it's fixed by hand.
//
// This script is idempotent: it can be safely re-run, e.g. if interrupted.
//
// Usage:
//   mongosh "<connection-string>" backend/scripts/migrate_ingredients.js

const unitLabelToEnumName = {
  'Stück': 'PIECE',
  'Teelöffel': 'TEASPOON',
  'Esslöffel': 'TABLESPOON',
  'Gramm': 'GRAM',
  'g': 'GRAM',
  'EL': 'TABLESPOON',
  'TL': 'TEASPOON',
  'ml': 'MILLILITER',
  'Zehen': 'CLOVE',
  'Prisen': 'PINCH',
  'Handvoll': 'HANDFUL',
  'Scheiben': 'SLICE',
  'Bund': 'BUNCH',
  'Kilo': 'KILOGRAM',
  'Liter': 'LITER',
  'Prise': 'PINCH',
  'Milliliter': 'MILLILITER',
  'Bündel': 'BUNCH',
};
const knownEnumNames = Object.values(unitLabelToEnumName);

db.Recipe.updateMany({}, [
  {
    $set: {
      ingredients: {
        $map: {
          input: { $ifNull: ['$ingredients', []] },
          as: 'ing',
          in: {
            name: { $ifNull: ['$$ing.name', '$$ing._id'] },
            normalizedKey: {
              $toLower: { $trim: { input: { $ifNull: ['$$ing.name', '$$ing._id'] } } },
            },
            quantity: '$$ing.quantity',
            unit: {
              $switch: {
                branches: Object.entries(unitLabelToEnumName).map(([label, enumName]) => ({
                  case: { $eq: ['$$ing.unit', label] },
                  then: enumName,
                })),
                default: '$$ing.unit',
              },
            },
          },
        },
      },
    },
  },
]);

// The IngredientName collection (popularity tracking) is no longer used anywhere
// in the codebase - drop it.
db.IngredientName.drop();

// Report any recipe whose unit values didn't map to a known label or enum name.
const unmapped = db.Recipe.aggregate([
  { $unwind: '$ingredients' },
  { $match: { 'ingredients.unit': { $nin: knownEnumNames } } },
  { $project: { _id: 1, title: 1, unit: '$ingredients.unit', ingredientName: '$ingredients.name' } },
]).toArray();

if (unmapped.length > 0) {
  print(`WARNING: ${unmapped.length} ingredient(s) have an unrecognized unit value and need manual review:`);
  printjson(unmapped);
} else {
  print('All ingredient unit values mapped successfully.');
}

print('Migration complete.');

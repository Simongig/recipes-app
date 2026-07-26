export function formatIngredientQuantity(ingredient) {
  return ingredient.unitAbbreviation
    ? `${ingredient.quantity} ${ingredient.unitAbbreviation}`
    : `${ingredient.quantity}`
}

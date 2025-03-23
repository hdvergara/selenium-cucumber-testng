Feature: Gestión del Carrito de Compras
  Como usuario de la tienda online,
  Quiero buscar un producto, agregarlo y removerlo del carrito,
  Para validar que el proceso de compra funcione correctamente.

  Scenario: Add and remove iPhone from the cart
    Given The user is on the homepage
    When They enter "iPhone" in the search bar and press search
    And They add the first search result to the cart
    And The user click on the shopping cart button
    And The press View Cart
    Then It is validated that the iPhone is in the shopping cart
    When The user remove the iPhone from the shopping cart
    Then It is validated that the iPhone is no longer in the shopping cart

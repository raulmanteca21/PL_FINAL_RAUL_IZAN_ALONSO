package casinoescape.ui;

import casinoescape.items.Inventory;
import casinoescape.items.Item;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class InventoryPanelView {
    private final VBox root = new VBox(6);
    private final ListView<String> items = new ListView<>();
    private final Label equippedWeapon = new Label();
    private final Label equippedArmor = new Label();
    private Inventory inventory;

    public InventoryPanelView() {
        root.setStyle("-fx-padding: 12; -fx-border-color: #d4af37; -fx-border-width: 3; -fx-background-color: #fff8df;");
        Label title = new Label("Caja de fichas");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #5c2300;");
        items.setPrefHeight(170);
        root.getChildren().addAll(title, equippedWeapon, equippedArmor, items);
    }

    public Node getNode() {
        return root;
    }

    public void refresh(Inventory inventory) {
        String selectedItemId = getSelectedItemId();
        this.inventory = inventory;
        items.getItems().clear();
        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.getItem(i);
            items.getItems().add(formatItem(item, inventory));
            if (item.getId().equals(selectedItemId)) {
                items.getSelectionModel().select(i);
            }
        }
        equippedWeapon.setText("Arma: " + (inventory.getEquippedWeapon() == null ? "ninguna" : inventory.getEquippedWeapon().getName()));
        equippedArmor.setText("Armadura: " + (inventory.getEquippedArmor() == null ? "ninguna" : inventory.getEquippedArmor().getName()));
    }

    private String formatItem(Item item, Inventory inventory) {
        String text = tagFor(item) + " " + item.getName();
        if (item == inventory.getEquippedWeapon() || item == inventory.getEquippedArmor()) {
            text += " [EQUIPADO]";
        }
        return text;
    }

    private String tagFor(Item item) {
        return switch (item.getType()) {
            case WEAPON -> "[ARMA]";
            case ARMOR -> "[ARMADURA]";
            case CONSUMABLE -> "[CONSUMIBLE]";
            case KEY -> "[LLAVE]";
        };
    }

    private String getSelectedItemId() {
        Item selected = getSelectedItem();
        return selected == null ? "" : selected.getId();
    }

    public Item getSelectedItem() {
        int index = items.getSelectionModel().getSelectedIndex();
        if (inventory == null || index < 0 || index >= inventory.size()) {
            return null;
        }
        return inventory.getItem(index);
    }
}

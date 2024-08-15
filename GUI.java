package pack;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;

import java.time.format.DateTimeFormatter;

import javafx.animation.FadeTransition;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class GUI extends Application {
    private Stage primaryStage;
    private Scene sceneFirst, sceneSecond, sceneThird, sceneFourth;
    private PasswordVault vault = new PasswordVault();
    private File selectedFile;
    private SecretKey currentKey;
    private byte[] currentSalt;
    private TableView<Password> tableView;

    // Clear sensitive data when the application stops
    @Override
    public void stop() {
        System.out.println("Application is closing...");
        clearSensitiveData();
    }

    // Start the application
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeScenes();
        primaryStage.setTitle("Password Management Tool");
        primaryStage.setScene(sceneFirst);
        primaryStage.show();
    }

    // Initialize the scenes
    private void initializeScenes() {
        initializeFirstScene();
        initializeSecondScene();
        initializeThirdScene();
        initializeForthScene();
    }

    // Home page for selecting a file, or generating a new file.
    private void initializeFirstScene() {
        // Button
        Button btnNF = new Button("New Vault");
        Button btnSF = new Button("Select Vault");
        btnSF.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");

            // Set initial directory to the current working directory
            File initialDirectory = new File(System.getProperty("user.dir"));
            fileChooser.setInitialDirectory(initialDirectory);

            selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                initializeThirdScene();
            }
        });

        // HBox
        HBox hboxButtons = new HBox();
        hboxButtons.setAlignment(Pos.CENTER); // Center the buttons horizontally
        hboxButtons.setSpacing(20);
        // VBox
        VBox vboxHomePage = new VBox();
        vboxHomePage.setAlignment(Pos.CENTER);
        vboxHomePage.setPadding(new Insets(50, 0, 0, 0));
        vboxHomePage.setSpacing(80);
        String buttonStyle = "-fx-background-color: #00b9ff; " + // Background
                "-fx-text-fill: #123561; " + // Text
                "-fx-background-radius: 20; " + // Elliptical border
                "-fx-padding: 5 12 5 12; " + // Padding around text
                "-fx-font-family: 'Montserrat'; " + // Font family
                "-fx-font-size: 15px;" + // Font size
                "-fx-font-weight: bold;" +
                "-fx-font-style: italic;" +
                "-fx-effect: dropshadow( three-pass-box , rgba(8,40,89,1) , 5, 0.0 , 0 , 1)";
        btnNF.setStyle(buttonStyle);
        btnNF.setPrefWidth(120);
        btnSF.setStyle(buttonStyle);
        btnSF.setPrefWidth(120);
        hboxButtons.getChildren().addAll(btnNF, btnSF);
        vboxHomePage.getChildren().add(hboxButtons);

        // BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vboxHomePage);

        // Set background
        String imageUrl1 = "back1.jpg";
        Image image1 = new Image(imageUrl1);
        BackgroundImage backgroundImageFirst = new BackgroundImage(image1,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false));
        borderPane.setBackground(new Background(backgroundImageFirst));

        sceneFirst = new Scene(borderPane, 350, 350);

        // Button event handler to switch to Scene 2
        btnNF.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.setScene(sceneSecond);
            }
        });
    }

    // Second page, after choosing "New File", user can enter a new file name and
    // master password and click the button to generate a new file
    private void initializeSecondScene() {
        // Button for Scene 2
        Button btn2BackTo1 = new Button("Go back to Home Page");
        btn2BackTo1.setStyle(
                "-fx-background-radius: 10; " +
                        "-fx-font-family: 'Lucida Bright'; " +
                        "-fx-font-size: 12px;");
        Button btnCreateNewFile = new Button("Create New Vault");
        String buttonStyleC = "-fx-background-color: #B5F0F1; " + // background
                "-fx-text-fill: black; " + // text
                "-fx-background-radius: 30; " + // Elliptical border
                "-fx-padding: 8 12 8 12; " + // Padding around text
                "-fx-font-family: 'Elephant'; " + // Font family
                "-fx-font-size: 14px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 3, 0.0, 0, 1)";
        btnCreateNewFile.setStyle(buttonStyleC);

        // Create TextFields for user input
        TextField tfFileName = new TextField();
        tfFileName.setPromptText("Enter Vault Name");

        TextField tfMasterPassword = new TextField();
        tfMasterPassword.setPromptText("Enter Master Password");

        // Create Labels
        Label lblFileNameDisplay = new Label();
        Label lblMasterPasswordDisplay = new Label();
        Label lblFileName = new Label("Vault Name:");
        lblFileName.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 1;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 14;" +
                        "-fx-padding: 5;");
        Label lblMasterPassword = new Label("Master Password:");
        lblMasterPassword.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 1;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 14;" +
                        "-fx-padding: 5;");

        // Event handlers to update labels when text fields lose focus
        tfFileName.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                lblFileNameDisplay.setText(tfFileName.getText());
            }
        });

        tfMasterPassword.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                lblMasterPasswordDisplay.setText(tfMasterPassword.getText());
            }
        });

        // VBox for the center content
        VBox vboxSecondPage = new VBox();
        vboxSecondPage.getChildren().addAll(
                lblFileName,
                tfFileName,
                lblFileNameDisplay,
                lblMasterPassword,
                tfMasterPassword,
                lblMasterPasswordDisplay,
                btnCreateNewFile);
        vboxSecondPage.setAlignment(Pos.CENTER);
        vboxSecondPage.setPadding(new Insets(10, 20, 20, 20));
        vboxSecondPage.setSpacing(5);

        // Main layout with BorderPane to position btn2BackTo1 at the top left
        BorderPane borderPaneSecondPage = new BorderPane();
        borderPaneSecondPage.setTop(btn2BackTo1);
        borderPaneSecondPage.setCenter(vboxSecondPage);

        BorderPane.setAlignment(btn2BackTo1, Pos.TOP_LEFT);
        BorderPane.setMargin(btn2BackTo1, new Insets(10)); // Margin for the button

        // Set background
        String imageUrl1 = "fp2.jpg";
        Image image1 = new Image(imageUrl1);
        BackgroundImage backgroundImageFirst = new BackgroundImage(image1,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false));
        borderPaneSecondPage.setBackground(new Background(backgroundImageFirst));

        // Create the scene with the BorderPane
        sceneSecond = new Scene(borderPaneSecondPage, 300, 280);

        // Button event handler to switch back to Scene Home Page
        btn2BackTo1.setOnAction(e -> primaryStage.setScene(sceneFirst));

        // Button event handler to Create New File
        btnCreateNewFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String fileName = tfFileName.getText().trim();
                String masterPassword = tfMasterPassword.getText();

                // Check if the file name and master password are empty
                if (fileName.isEmpty() || masterPassword.isEmpty()) {
                    showAlert("Error", "Vault name and master password can't be empty.", Alert.AlertType.ERROR);
                    return;
                }

                try {
                    // Create a new PasswordVault object
                    PasswordVault vault = new PasswordVault();

                    // Generate a random salt
                    byte[] salt = CryptoUtils.generateSalt();

                    // Use the master password and salt to derive a secret key
                    SecretKey key = CryptoUtils.deriveKeyFromPassword(masterPassword, salt);

                    // Save the PasswordVault object to a file
                    FileIO.saveToFile(vault, fileName, key, salt);

                    showAlert("Success", "New Vault is successfully created.", Alert.AlertType.INFORMATION);

                    primaryStage.setScene(sceneFirst); // Switch back to the first scene

                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error", "Error creating new Vault.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    // Third page, after selecting a file, user can enter the master password to
    // unlock the file
    private void initializeThirdScene() {
        Label label = new Label("Please enter the master password:");
        label.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 1;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 14;" +
                        "-fx-padding: 5;");
        PasswordField passwordField = new PasswordField();

        Button btn3BackTo1 = new Button("Go back to Home Page");
        btn3BackTo1.setStyle(
                "-fx-background-radius: 10; " +
                        "-fx-font-family: 'Lucida Bright'; " +
                        "-fx-font-size: 12px;");
        btn3BackTo1.setOnAction(e -> primaryStage.setScene(sceneFirst));
        Button confirmButton = new Button("Confirm");
        String buttonStyle = "-fx-background-color: #B5F0F1; " +
                "-fx-text-fill: black; " +
                "-fx-background-radius: 30; " +
                "-fx-padding: 10 20 10 20; " +
                "-fx-font-family: 'Elephant'; " +
                "-fx-font-size: 16px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 3, 0.0, 0, 1)";
        confirmButton.setStyle(buttonStyle);
        confirmButton.setOnAction(e -> {
            String masterPassword = passwordField.getText();
            if (!masterPassword.isEmpty()) {
                try {
                    // Use the master password to unlock the vault
                    vault = loadPasswordVault(selectedFile, masterPassword);

                    // transition to the next scene
                    FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), sceneFourth.getRoot());
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                    primaryStage.setScene(sceneFourth);
                    updatePasswordTableView(vault);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert("Error", "Failed to load vault: " + selectedFile.getPath(), Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Error", "Master password cannot be empty.", Alert.AlertType.ERROR);
            }
        });

        VBox vboxThirdPage = new VBox(10);
        vboxThirdPage.getChildren().addAll(label, passwordField, confirmButton);
        vboxThirdPage.setAlignment(Pos.CENTER);
        vboxThirdPage.setPadding(new Insets(2, 20, 10, 20));

        // Main layout with BorderPane to position btn2BackTo1 at the top left
        BorderPane borderPaneThirdPage = new BorderPane();
        borderPaneThirdPage.setTop(btn3BackTo1);
        borderPaneThirdPage.setCenter(vboxThirdPage);

        BorderPane.setAlignment(btn3BackTo1, Pos.TOP_LEFT);
        BorderPane.setMargin(btn3BackTo1, new Insets(10)); // Margin for the button

        // Set background
        String imageUrl1 = "fp2.jpg";
        Image image1 = new Image(imageUrl1);
        BackgroundImage backgroundImageFirst = new BackgroundImage(image1,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false));
        borderPaneThirdPage.setBackground(new Background(backgroundImageFirst));

        sceneThird = new Scene(borderPaneThirdPage, 300, 200);
        primaryStage.setScene(sceneThird);
    }

    // Fourth page, after unlocking the file, user can view and edit passwords
    private void initializeForthScene() {
        BorderPane borderPane = new BorderPane();

        // Create a button bar
        HBox buttonBar = new HBox(10);
        buttonBar.setPadding(new Insets(15, 12, 15, 12));

        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");
        Button updateButton = new Button("Edit");
        Button saveButton = new Button("Save");
        Button returnHomeButton = new Button("Return Home");

        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setPrefWidth(120);

        // Add buttons to the button bar
        buttonBar.getChildren().addAll(addButton, deleteButton, updateButton, searchField, saveButton,
                returnHomeButton);

        // Give the button bar a style
        buttonBar.setSpacing(8);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #7CBAC9, #A1A4A5);");

        for (Node node : buttonBar.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setStyle("-fx-background-color: #D3F0F7; " +
                        "-fx-text-fill: #04143D; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 6 10 6 10; " +
                        "-fx-font-family: 'Lucida Bright'; " +
                        "-fx-font-size: 13px;" +
                        "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 );");
            }
        }

        tableView = new TableView<>();

        // Create columns for the table view
        TableColumn<Password, String> urlCol = new TableColumn<>("URL");
        urlCol.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlCol.setPrefWidth(95);

        TableColumn<Password, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(95);
        usernameCol.setStyle("-fx-alignment: center;");

        TableColumn<Password, String> passwordCol = new TableColumn<>("Password");
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        passwordCol.setPrefWidth(95);
        // Custom cell factory to display password as asterisks
        passwordCol.setCellFactory(tc -> new TableCell<>() {
            private final ContextMenu contextMenu = new ContextMenu();
            private final MenuItem copyMenuItem = new MenuItem("Copy");

            {
                contextMenu.getItems().add(copyMenuItem);
                copyMenuItem.setOnAction((ActionEvent event) -> {
                    String password = getItem();
                    if (password != null && !password.isEmpty()) {
                        final Clipboard clipboard = Clipboard.getSystemClipboard();
                        final ClipboardContent content = new ClipboardContent();
                        content.putString(password);
                        clipboard.setContent(content);
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                    setContextMenu(null);
                } else {
                    setText(String.valueOf("*").repeat(item.length()));
                    Tooltip tooltip = new Tooltip(item);
                    tooltip.setShowDelay(Duration.seconds(0.5));
                    setTooltip(tooltip);
                    setContextMenu(contextMenu);
                }
            }
        });

        TableColumn<Password, String> strengthCol = new TableColumn<>("Strength");
        strengthCol.setCellValueFactory(cellData -> {
            Password password = cellData.getValue();
            double strengthValue = password.getStrength();
            String strengthText = PasswordUtils.getStrengthText(strengthValue);
            return new ReadOnlyStringWrapper(strengthText);
        });
        strengthCol.setPrefWidth(85);
        strengthCol.setCellFactory(column -> new TableCell<Password, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    // Set the text color based on the strength
                    if (item.equals("Strong")) {
                        setStyle("-fx-text-fill: green; -fx-alignment: center;");
                    } else if (item.equals("Medium")) {
                        setStyle("-fx-text-fill: #D88200; -fx-alignment: center;");
                    } else {
                        setStyle("-fx-text-fill: #C4272F; -fx-alignment: center;");
                    }
                }
            }
        });

        TableColumn<Password, String> editTimeCol = new TableColumn<>("Edit Time");
        editTimeCol.setCellValueFactory(cellData -> {
            LocalDateTime editDate = cellData.getValue().getEditDate();
            String formattedDate = editDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return new ReadOnlyStringWrapper(formattedDate);
        });
        editTimeCol.setPrefWidth(135);

        tableView.getColumns().addAll(urlCol, usernameCol, passwordCol, strengthCol, editTimeCol);
        tableView.setStyle("-fx-border-color: #0B453F; -fx-font-family: 'Roboto Light'; -fx-font-size: 14px;");
        // tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableView.setRowFactory(tv -> new TableRow<Password>() {
            @Override
            protected void updateItem(Password item, boolean empty) {
                super.updateItem(item, empty);
                setStyle("-fx-padding: 3px;");
            }
        });

        addButton.setOnAction(e -> {
            // Create a new dialog for adding a password
            Dialog<Password> dialog = new Dialog<>();
            dialog.setTitle("Add New Password");
            dialog.setHeaderText("Fill in the information for the new password.");

            // Button types
            ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

            // Create a grid pane for the dialog
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField urlField = new TextField();
            urlField.setPromptText("URL");
            TextField usernameField = new TextField();
            usernameField.setPromptText("Username");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");
            Label strengthLabel = new Label("Password Strength:");
            Label strengthTextLabel = new Label(" ");
            ProgressBar strengthBar = new ProgressBar(0);
            Button generatePasswordButton = new Button("Generate Password");

            grid.add(new Label("URL:"), 0, 0);
            grid.add(urlField, 1, 0);
            grid.add(new Label("Username:"), 0, 1);
            grid.add(usernameField, 1, 1);
            grid.add(new Label("Password:"), 0, 2);
            grid.add(passwordField, 1, 2);
            grid.add(generatePasswordButton, 2, 2);
            grid.add(strengthLabel, 0, 3);
            grid.add(strengthTextLabel, 1, 3);
            grid.add(strengthBar, 2, 3);

            dialog.getDialogPane().setContent(grid);

            // Update the strength bar when the password field changes
            passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
                double strengthValue = PasswordUtils.calculatePasswordStrength(newValue);
                strengthBar.setProgress(strengthValue);
                String strengthText = PasswordUtils.getStrengthText(strengthValue);
                strengthTextLabel.setText(strengthText);
            });

            // Button event handler
            generatePasswordButton.setOnAction(event -> {
                showPasswordGenerationDialog(passwordField, strengthBar);
            });

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButtonType) {
                    Password newPassword = new Password(
                            usernameField.getText(),
                            urlField.getText(),
                            passwordField.getText());

                    double strength = PasswordUtils.calculatePasswordStrength(passwordField.getText());
                    newPassword.setStrength(strength);

                    newPassword.setEditDate(LocalDateTime.now());

                    return newPassword;
                }
                return null;
            });

            // Show the dialog and handle the result
            Optional<Password> result = dialog.showAndWait();

            result.ifPresent(password -> {
                vault.addPassword(password);
                tableView.getItems().add(password);
            });
        });

        deleteButton.setOnAction(e -> {
            // Select the password to delete
            Password selectedPassword = tableView.getSelectionModel().getSelectedItem();

            if (selectedPassword != null) {
                // Delete the password from the vault
                vault.deletePassword(selectedPassword.getIdentifier());

                // Remove the password from the table view
                tableView.getItems().remove(selectedPassword);

                // Show message
                showAlert("Success", "Password deleted successfully.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "No password selected.", Alert.AlertType.ERROR);
            }
        });

        updateButton.setOnAction(e -> {
            Password selectedPassword = tableView.getSelectionModel().getSelectedItem();

            if (selectedPassword != null) {
                Dialog<Password> dialog = createPasswordUpdateDialog(selectedPassword);
                Optional<Password> result = dialog.showAndWait();

                result.ifPresent(newPassword -> {
                    // Update the password in the vault
                    vault.updatePassword(selectedPassword.getIdentifier(), newPassword.getPassword());

                    // Set the other fields in the selected password
                    selectedPassword.setUsername(newPassword.getUsername());
                    selectedPassword.setPassword(newPassword.getPassword());
                    selectedPassword.setEditDate(LocalDateTime.now());
                    selectedPassword.setStrength(PasswordUtils.calculatePasswordStrength(newPassword.getPassword()));

                    // Refresh the tableView to reflect the update
                    tableView.refresh();

                    showAlert("Success", "Password updated successfully.", Alert.AlertType.INFORMATION);
                });
            } else {
                // Handle case where no password is selected
                showAlert("Error", "No password selected.", Alert.AlertType.ERROR);
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = searchField.getText();
            List<Password> filteredPasswords = vault.searchPasswords(searchText);
            tableView.setItems(FXCollections.observableArrayList(filteredPasswords));
        });

        saveButton.setOnAction(e -> {
            try {
                if (selectedFile != null && currentKey != null && currentSalt != null) {
                    FileIO.saveToFile(vault, selectedFile.getPath(), currentKey, currentSalt);
                    showAlert("Success", "Vault is successfully saved.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "No file selected or missing encryption key/salt.", Alert.AlertType.ERROR);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to save vault: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Return to the home page
        returnHomeButton.setOnAction(e -> primaryStage.setScene(sceneFirst));

        // Add the button bar and table view to the BorderPane
        borderPane.setTop(buttonBar);
        borderPane.setCenter(tableView);

        // Create the scene
        sceneFourth = new Scene(borderPane, 520, 400);
        primaryStage.setScene(sceneFourth);
    }

    // Utility method to show a dialog for password generation
    private void showPasswordGenerationDialog(TextField passwordField, ProgressBar strengthBar) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Generate Password");

        // Set the button types
        ButtonType generateButtonType = new ButtonType("Generate", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(generateButtonType, ButtonType.CANCEL);

        // Create a grid pane for the dialog
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField lengthField = new TextField();
        lengthField.setPromptText("Length");
        CheckBox includeDigits = new CheckBox("Include Digits");
        includeDigits.setSelected(true);
        CheckBox includeUpperCase = new CheckBox("Include Upper Case");
        includeUpperCase.setSelected(true);
        CheckBox includeLowerCase = new CheckBox("Include Lower Case");
        includeLowerCase.setSelected(true);
        CheckBox includeSymbols = new CheckBox("Include Symbols");
        includeSymbols.setSelected(true);

        // Add components to the grid pane
        gridPane.add(new Label("Length:"), 0, 0);
        gridPane.add(lengthField, 1, 0);
        gridPane.add(includeDigits, 0, 1);
        gridPane.add(includeUpperCase, 0, 2);
        gridPane.add(includeLowerCase, 0, 3);
        gridPane.add(includeSymbols, 0, 4);

        dialog.getDialogPane().setContent(gridPane);

        // Set the result converter for the dialog
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == generateButtonType) {
                int length = Integer.parseInt(lengthField.getText());
                boolean digits = includeDigits.isSelected();
                boolean upperCase = includeUpperCase.isSelected();
                boolean lowerCase = includeLowerCase.isSelected();
                boolean symbols = includeSymbols.isSelected();

                String generatedPassword = PasswordUtils.generatePassword(length, lowerCase, upperCase, digits,
                        symbols);
                passwordField.setText(generatedPassword);
                strengthBar.setProgress(PasswordUtils.calculatePasswordStrength(generatedPassword));
            }
            return null;
        });

        dialog.showAndWait();
    }

    // Utility method to display alerts
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Utility method to load PasswordVault from a file
    private PasswordVault loadPasswordVault(File file, String masterPassword) throws Exception {
        byte[] fileData = Files.readAllBytes(file.toPath());

        // Get the salt from the file data
        byte[] salt = Arrays.copyOfRange(fileData, 0, CryptoUtils.SALT_LENGTH);

        // Use the master password and salt to derive a secret key
        SecretKey key = CryptoUtils.deriveKeyFromPassword(masterPassword, salt);

        // Store the derived key and salt in the class members for future use
        if (currentSalt == null && currentKey == null) {
            currentSalt = salt;
            currentKey = key;
        }

        // Load the PasswordVault object from the file data
        return FileIO.loadFromFile(file.getPath(), key);
    }

    // Method to open a file
    public void openFile(String password, File file) {
        try {
            PasswordVault vault = loadPasswordVault(file, password);
            this.vault = vault;

            updatePasswordTableView(vault);
        } catch (Exception e) {
            e.printStackTrace();
            // Display an error message if the file cannot be opened
            showAlert("Error", "Failed to open file: " + file.getPath(), Alert.AlertType.ERROR);
        }
    }

    // Method to save a file
    public void saveFile(String filePath) {
        try {
            FileIO.saveToFile(vault, filePath, currentKey, currentSalt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to update the table view with the passwords from the vault
    private void updatePasswordTableView(PasswordVault vault) {
        List<Password> passwordList = vault.getPasswords();
        // transform the list into an ObservableList
        ObservableList<Password> observablePasswordList = FXCollections.observableArrayList(passwordList);

        tableView.setItems(observablePasswordList);
        tableView.refresh();
    }

    // Method to create a dialog for updating a password
    private Dialog<Password> createPasswordUpdateDialog(Password selectedPassword) {
        Dialog<Password> dialog = new Dialog<>();
        dialog.setTitle("Update Password");
        dialog.setHeaderText("Update password details.");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField usernameField = new TextField(selectedPassword.getUsername());
        usernameField.setPromptText("Username");

        TextField passwordField = new TextField(selectedPassword.getPassword());
        passwordField.setPromptText("Password");

        ProgressBar strengthBar = new ProgressBar(0);
        Button generatePasswordButton = new Button("Generate");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Password Strength:"), 0, 2);
        grid.add(generatePasswordButton, 2, 1);
        grid.add(strengthBar, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Set the result converter for the dialog
        generatePasswordButton.setOnAction(event -> {
            // Use the utility method to show the password generation dialog
            showPasswordGenerationDialog(passwordField, strengthBar);
        });

        // Update the strength bar when the password field changes
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            double strength = PasswordUtils.calculatePasswordStrength(newValue);
            strengthBar.setProgress(strength);
        });

        // Set the result converter for the dialog
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                // Create a new Password object with updated values
                Password updatedPassword = new Password(
                        usernameField.getText(),
                        selectedPassword.getUrl(), // Presumably this won't change
                        passwordField.getText());
                updatedPassword.setStrength(PasswordUtils.calculatePasswordStrength(passwordField.getText()));
                updatedPassword.setEditDate(LocalDateTime.now()); // Set the edit date to now
                return updatedPassword;
            }
            return null;
        });

        return dialog;
    }

    // Method to clear sensitive data
    public void clearSensitiveData() {
        this.currentKey = null;
        this.currentSalt = null;
    }
}
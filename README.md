## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).


# Guide de Configuration et d'Exécution
1. Prérequis
Assurez-vous d'avoir les éléments suivants installés :
- Java Development Kit (JDK) : Version 23.
- Installer l'Extension Pack pour Java sur Visual Code

# Configuration du projet
1. Installez Java JDK 17 ou plus récent.
2. Placez le SDK JavaFX dans le dossier `lib/javafx-sdk-23.0.1/`.
3. Importez le projet dans VS Code et assurez-vous que les extensions Java sont installées.
4. Utilisez la configuration suivante pour exécuter le projet :
   - `--module-path ./lib/javafx-sdk-23.0.1/lib --add-modules javafx.controls,javafx.fxml`



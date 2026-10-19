# Changelog

All notable changes to the Dynamic Football Simulator are documented in this file.

## [v1.1]

### Fixed

- **World Cup and League Career save loading:** Fixed an issue where loading a saved game after completing a season did not correctly restore the saved simulation state.
- **Invalid input in Load Game:** Added input validation to prevent non-numeric save selections from causing `NumberFormatException` and terminating the application.
- **Unexpected end-of-file:** Improved handling of missing console input in the shared `Input` class to prevent `NoSuchElementException` when reading user input or waiting for Enter.
- **Invalid input in Delete Game:** Added validation to handle non-numeric save selections without crashing the application.
- **End-of-file when no saves are available:** Improved handling of unexpected input termination when loading games, allowing the application to exit the menu operation gracefully.
- **Save filename validation:** Added validation to restrict save names to permitted characters and prevent unsafe file paths.
- **Save-file listing errors:** Added explicit handling for cases where the save directory cannot be listed, preventing an unexpected `NullPointerException`.
- **Out-of-range save selections:** Prevented loading or deleting saves when the selected number does not correspond to an available save.
- **Duplicate save names:** Prevented existing save files from being overwritten by prompting the user to choose a different name.

### Improved

- **Input validation:** Improved handling of invalid user input across save management menus.
- **Error handling:** Added clearer error messages and safer handling of invalid save selections and save names.
- **Save management:** Improved the reliability of saving, loading, and deleting games.


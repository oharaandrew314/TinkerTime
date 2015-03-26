Contributor Readme
==================

Learn how to set up your environment to begin contributing to this repo.

### Requirements
- JDK 7 or better
- gradle
  - install in repo with `gradlew.bat` or `./gradlew`

### Download Code
This project uses a submodule for common code between my projects
```
git clone git@github.com:oharaandrew314/TinkerTime.git
git submodule init
git submodule update
```

### Set up Dependencies

- This project requires JDK 7 or better
  - All code must comply with the 1.7 grammar standard
- This project uses gradle, which manages dependencies for you
  - see `build.gradle`
- To build the project with gradle, run `gradle clean assemble`
- To set up an eclipse project (and install dependencies), run `gradle eclipse`

### Code Style
- Try to keep lines of code to 120 characters or less
- Tabbed indentation so that tab width can be tailored in each dev's editor

### Testing
- Please run tests with **gradle** before making a PR
  - `gradle clean check`

### Distribution
Gradle is used for building distributables
- To build a jar, run `gradle jar`, then see `build/libs` dir
- To build a zip with Readme, run `gradle release`, then see `build/distributions`






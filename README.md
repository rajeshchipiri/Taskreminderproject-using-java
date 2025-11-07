# Task Reminder (Core Java)

Small CLI task reminder. Files are stored in `tasks.csv` next to the working directory.

How to compile (Windows cmd):

```bat
cd /d "c:\Users\rajes\OneDrive\New folder\New folder\New folder (3)\task remainder"
mkdir out 2>nul
javac -d out src\com\taskreminder\**\*.java
```

Run:

```bat
java -cp out com.taskreminder.Main
```

There is a small `run.bat` included that compiles to `out` and prints how to run.

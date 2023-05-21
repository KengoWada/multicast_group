# Distributed Systems Coursework

A simple implementation of group communication with `MulticastSocket`

## How Run

- Clone the repo and change to that folder

- Compile the files

```sh
javac *.java
```

- Now execute the code

```sh
# To run the client
java -cp . MulticastGroup <group-name> <username>

# To run the group monitor
java -cp . MulticastGroupMonitor <group-name>
```

- **Note**:

  - Valid group names: `timetable`, `exams`, `classes`
  
  - Type `.exit` to leave the group.

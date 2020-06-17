# DNASequencesHashTable
Database system for DNA sequences.

# Summary
This database system will include a disk-based hash table using a simple bucket hash, to support searches by sequence identifier. The bulk of the database will be stored in a binary file on disk, with a memory manager that stores both sequences and sequenceIDs. Data records that are stored consist of two parts. The first part will be the identifier. The identifier is a relatively short string of characters from the A, C, G, T alphabet. The second part is the sequence. The sequence is a relatively long string (could be thousands of characters) from the A, C, G, T alphabet.

# Invocation
java DNAdbase "name of command file" "name of hash file" "size of hash table" "memory file"
</br></br>
command file: holds the commands to be processed by the program</br>
hash-file: the name of the output file that will store the required sequences.</br>
Hash table size: size of the hash table. Must be a multiple of 32. </br>
Memory file: Name of the file used by the memory manager to gold strings.

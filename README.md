# Merkle Tree FastMerkleTree
![image](https://github.com/blueokanna/MerkleTreeAPI/assets/56761243/05d496fd-0c45-4fef-ab88-852786cfa65b)
[![Apache Java](https://img.shields.io/badge/logo-apache-yellow?logo=apache-maven)](https://www.apache.org/foundation/marks/)
[![License](http://img.shields.io/:license-apache-green.svg?style=flat)](https://www.apache.org/licenses/)
[![Hits](https://hits.sh/github.com/blueokanna/MerkleTreeAPI.git.svg?color=fe7d37)](https://hits.sh/github.com/blueokanna/MerkleTreeAPI.git/)

----

> **Please note that this Java project is based on JDK 17**
 
### Introducing the FastMerkleTree
> MerkleTree is a binary tree data structure mainly used to store and validate large amounts of data efficiently. Merkle trees are used in a wide range of application scenarios, for example, this structure is used in the Bitcoin blockchain system. The Bitcoin blockchain uses MerkleTree to store all transaction records, which ensures data integrity and security. Of course, Merkle trees are also widely used in distributed systems and in some areas of cryptography.


### Comparison to other **MerkleTrees**
1. Compared to other **MerkleTree** projects, this project provides parallel asynchronous hash computation, which allows the project to run faster and get results. 2.
2. for other **MerkleTree**, the time complexity is basically **O(n) + O(log2(n))** or **O(n)**, in this project, it has a much faster and optimised time complexity **O(log n)**. 3. for other projects, here is a comparison of the project with other **MerkleTree** projects.
3. For other projects, the interface here is richer than some traditional **MerkleTree**, with ``the ability to customise the number of threads needed, and more options for safe digest algorithms'' (described below).

### The Java project calls FastMerkleTree from this project:
**Maven Dependency**.
```
<dependency>
    <groupId>gay.blueokanna</groupId>
    <artifactId>merkletreeapi</artifactId>
    <version>0.0.1</version>
</dependency
```
**Gradle:**
```
 implementation group: 'gay.blueokanna', name: 'merkletreeapi', version: '0.0.1'
```
**Gradle(Kotlin)**
```
implementation("gay.blueokanna:merkletreeapi:0.0.1")
```
**ivy:**
```
<dependency org="gay.blueokanna" name="merkletreeapi" rev="0.0.1"/>
```
**sbt:**
```
libraryDependencies += "gay.blueokanna" % "merkletreeapi" % "0.0.1"
```

### How FastMerkleTree is used:
**The summary algorithms that can be used are these:**
```
MD5
SHA1
SHA224
SHA256
SHA384
SHA512
SHA3
Whirlpool
RIPEMD160
```

**MerkleTree libraries used by default, the following SHA-256 by default and fully threaded**
```
Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(); // fully threaded computing
```
```
public static void main(String... args){
  MerkleTree merkleTrees = new MerkleTree(); //initialise MerkleTree
  merkleTrees.addBlock("Block1"); //add Block1
  merkleTrees.addBlock("Block2"); //add Block2
  merkleTrees.addBlock("Block3"); //add Block3
  merkleTrees.addBlock("Block4"); //add Block4
  System.out.println("MerkleTree Root: " + merkleTrees.computeRootHash());
  merkleTrees.shutdown(); // here the computation is done, shutting down the use of threads
}

The output MerkleTree Root result is: ae6d557bddd73e31cf344e2f7a1eb52805dc1eeae489a1b62d8f701535978cf5
```
> SHA256 four blocks can be verified at [Hash Calculator](https://blockchain-academy.hs-mittweida.de/merkle-tree/)

**Custom use of the MerkleTree library, following custom digest algorithms as well as full-threaded usage**
```
public static void main(String... args){
  MerkleTree merkleTrees = new MerkleTree("Whirlpool"); //Initialise MerkleTree, use Whirlpool digest algorithm.
  merkleTrees.addBlock("Block1"); //add Block1
  merkleTrees.addBlock("Block2"); //add Block2
  merkleTrees.addBlock("Block3"); //add Block3
  merkleTrees.addBlock("Block4"); //add Block4
  System.out.println("MerkleTree Root: " + merkleTrees.computeRootHash());
  merkleTrees.shutdown(); // here the computation is done, shutting down the use of threads
}

The output MerkleTree Root result is:
954bc93569176cb8b8097381a48a3423b4a98dab9c04276cbcea6f439bc860260b9bbfdb030f75621527c445e962289c063177eb4739fbee500f86ac84e11738
```

```
public static void main(String... args){
  MerkleTree merkleTrees = new MerkleTree("RIPEMD160", 8); //Initialise MerkleTree, using the RIPEMD160 digest algorithm, and using 8 threads.
  merkleTrees.addBlock("Block1"); //add Block1
  merkleTrees.addBlock("Block2"); //add Block2
  merkleTrees.addBlock("Block3"); //add Block3
  merkleTrees.addBlock("Block4"); //add Block4
  System.out.println("MerkleTree Root: " + merkleTrees.computeRootHash());
  merkleTrees.shutdown(); // here the computation is done, shutting down the use of threads
}

The output MerkleTree Root result is: 6024b95047cb52e80a87ed3b429d05acd00999d9
```
----

> You can design code to continuously generate 10000 Blocks, named Block1, Block2, Block3......, Block10000, through a for loop. , Block10000, and add these 10000 blocks to get the final block value.

For the author's computer **(full-threaded SHA256 algorithm)**, the time taken to generate 10,000 is **00:00:734**, and the total time taken at the end is **00:00:881** By the simple time difference, it is also possible to know that the summation of the MerkleTree obtained by the computation yields the hash value of the final root in total computation time **00:00: 147***, and the total computation time of the hash value of the final root is **00:00: 147***. 147**

### Principle (SHA-256 is used here)
The hash value of the two blocks will be counted out and added up, from 4 -> 2 -> 1 in this order, if there is an odd number of Blocks, then it will start from the left, two by two, for example, there are five blocks, then the four blocks from the left to the right will be merged into two, if there is still an odd number of blocks, continue to combine two by two from the left to the right, and the original remaining one block upward until there are an odd number of blocks, the two by two to generate If there are still an odd number of blocks, then the four blocks on the left will be merged into two, and if there is still an odd number of blocks, then the two blocks will be merged into two.

----
Finally, thanks to **@bcgit** for the **Bouncycastle** encryption algorithm!

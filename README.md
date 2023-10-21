# 默克尔树 FastMerkleTree
![image](https://github.com/blueokanna/MerkleTreeAPI/assets/56761243/05d496fd-0c45-4fef-ab88-852786cfa65b)
![Github Action Pass](https://github.com/github/docs/actions/workflows/main.yml/badge.svg)

----

> **请注意这个 Java 项目是基于 JDK 17 的**
 
### 介绍 FastMerkleTree
> MerkleTree 是一种二叉树数据结构，主要用于高效地存储和验证大量数据。默克尔树的应用场景非常广泛，例如在比特币区块链系统中就采用这个结构。比特币区块链使用 MerkleTree 来存储所有的交易记录，这样可以确保数据的完整性和安全性。当然在分布式的系统和一些密码学领域，默克尔树也有着广泛的应用。


### 对于其他的 **MerkleTree** 对比
1. 对比于其他的 **MerkleTree** 项目，本项目提供的是采用并行异步计算哈希值，让项目更快的运行进而得到项目结果。
2. 对于其他的 **MerkleTree** ，其时间复杂度基本上为 **O(n) + O(log2(n))** 或者是  **O(n)**，在本项目中，其具有更快的速度以及优化后的时间复杂度**O(log n)**
3. 对于其他的项目，这里的接口相比于一些传统的 **MerkleTree** 更加丰富，`可以自定义需要的线程数，以及更多的安全摘要算法的选择`（以下会介绍）

### Java 项目调用本项目的 FastMerkleTree：
**Maven Dependency**:
```
<dependency>
    <groupId>gay.blueokanna</groupId>
    <artifactId>merkletreeapi</artifactId>
    <version>0.0.1</version>
</dependency>
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

### FastMerkleTree 的使用方法：
**可以使用的摘要算法有以下这些：**
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

**默认使用的 MerkleTree 库，以下为默认的 SHA-256 以及全线程的使用**
```
Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(); //全线程运算
```
```
public static void main(String... args){
  MerkleTree merkleTrees = new MerkleTree(); //初始化 MerkleTree
  merkleTrees.addBlock("Block1");  //添加Block1
  merkleTrees.addBlock("Block2");  //添加Block2
  merkleTrees.addBlock("Block3");  //添加Block3
  merkleTrees.addBlock("Block4");  //添加Block4
  System.out.println("MerkleTree Root: " + merkleTrees.computeRootHash());
  merkleTrees.shutdown();        //这里计算完成，关闭线程的使用
}

输出的 MerkleTree Root 结果为：ae6d557bddd73e31cf344e2f7a1eb52805dc1eeae489a1b62d8f701535978cf5
```
> SHA256 四个区块可以验证的网站：https://blockchain-academy.hs-mittweida.de/merkle-tree/

**自定义使用的 MerkleTree 库，以下为自定义摘要算法以及全线程的使用**
```
public static void main(String... args){
  MerkleTree merkleTrees = new MerkleTree("Whirlpool"); //初始化 MerkleTree,使用 Whirlpool 摘要算法
  merkleTrees.addBlock("Block1");  //添加Block1
  merkleTrees.addBlock("Block2");  //添加Block2
  merkleTrees.addBlock("Block3");  //添加Block3
  merkleTrees.addBlock("Block4");  //添加Block4
  System.out.println("MerkleTree Root: " + merkleTrees.computeRootHash());
  merkleTrees.shutdown();        //这里计算完成，关闭线程的使用
}

输出的 MerkleTree Root 结果为：
954bc93569176cb8b8097381a48a3423b4a98dab9c04276cbcea6f439bc860260b9bbfdb030f75621527c445e962289c063177eb4739fbee500f86ac84e11738
```

```
public static void main(String... args){
  MerkleTree merkleTrees = new MerkleTree("RIPEMD160"，8); //初始化 MerkleTree,使用 RIPEMD160 摘要算法，并且使用 8 个线程
  merkleTrees.addBlock("Block1");  //添加Block1
  merkleTrees.addBlock("Block2");  //添加Block2
  merkleTrees.addBlock("Block3");  //添加Block3
  merkleTrees.addBlock("Block4");  //添加Block4
  System.out.println("MerkleTree Root: " + merkleTrees.computeRootHash());
  merkleTrees.shutdown();        //这里计算完成，关闭线程的使用
}

输出的 MerkleTree Root 结果为：6024b95047cb52e80a87ed3b429d05acd00999d9
```
----

> 可以设计代码，通过 for 循环，连续生成 10000 个 Block，名字为 Block1, Block2， Block3......，Block10000，将这 10000 个区块相加获得最后的区块值。

对于作者的电脑来说 **（全线程的 SHA256 算法）**，生成一万个花费的时间为 **00:00:734**，最后总花费的时间为 **00:00:881** 通过简单的时间差也就可以知道计算得到的 MerkleTree 的相加得到最后根的哈希值总计算时间为 **00:00:147**

### 原理介绍（这里使用 SHA-256）
将两个区块的哈希值分别算出来，加起来，由 4 -> 2 -> 1 这样的顺序，如果为奇数个 Block 那么它将从左边开始，两两结合，比如有五个区块，那么从左向右的四个区块会合并成两个，如果还是出现奇数个区块，继续从左向右两两结合，原来还剩下的一个区块向上传递，直到出现奇数个 Block 的时候，两两结合生成 **MerkleTree** 的区块，一般来说这个区块的父区块为 root 区块，也就是我们所求的。

----
最后感谢 **@bcgit** 的 **Bouncycastle** 的提供的加密算法

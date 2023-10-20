# 默克尔树 MerkleTree (基于时间复杂度为 O(n))

----

> **请注意这个项目是基于 JDK 17 的**
 
### 介绍 MerkleTree
> MerkleTree 是一种二叉树数据结构，主要用于高效地存储和验证大量数据。默克尔树的应用场景非常广泛，例如在比特币区块链系统中就采用这个结构。比特币区块链使用 MerkleTree 来存储所有的交易记录，这样可以确保数据的完整性和安全性。当然在分布式的系统和一些密码学领域，默克尔树也有着广泛的应用。

### MerkleTree 的使用
如果是直接使用 MerkleTree 的 **jar** 包，那么在你的项目中直接导入已经打包好的 **jar** 包：
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

> 可以验证的网站：https://blockchain-academy.hs-mittweida.de/merkle-tree/
```
----

> 通过 for 循环，连续生成 10000 个 Block，名字为 Block1, Block2， Block3......，Block10000。

对于作者的电脑来说，生成一万个花费的时间为 **00:00:734**，最后总花费的时间为 **00:00:881** 通过简单的时间差也就可以知道计算得到的 MerkleTree 的相加得到最后根的哈希值总计算时间为 **00:00:147**

### 原理介绍（这里使用 SHA-256）
将两个区块的哈希值分别算出来，加起来，由 4 -> 2 -> 1 这样的顺序，如果为奇数个 Block 那么它将从左边开始，两两结合，比如有五个区块，那么从左向右的四个区块会合并成两个，如果还是出现奇数个区块，继续从左向右两两结合，原来还剩下的一个区块向上传递，直到出现奇数个 Block 的时候，两两结合生成 Merkle Tree 的区块，一般来说这个区块的父区块为 root 区块，也就是我们所求的。


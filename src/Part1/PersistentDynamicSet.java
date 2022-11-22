package Part1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class PersistentDynamicSet<E> extends BinarySearchTree<E> {
  public static void main(String[] args) {
    PersistentDynamicSet<String> tree = new PersistentDynamicSet<>();
    tree.add("cap");
    tree.add("god");
    tree.add("age");
    tree.add("job");
    tree.add("nut");
    tree.add("dam");
    tree.add("egg");
    tree.add("bar");
    tree.add("pig");
    tree.add("fat");
    tree.remove("god");
    tree.remove("cap");
    tree.remove("age");
    tree.remove("bar");
    tree.remove("egg");
    tree.remove("pig");

    JFrame frame = new JFrame("Binary Search Tree");
    tree.initPanel(tree.trees.get(tree.nums));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(tree.mainPanel);
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension dimension = toolkit.getScreenSize();
    int screenHeight = dimension.height;
    int screenWidth = dimension.width;
    frame.pack();
    frame.setLocation(new Point((screenWidth / 2) - (frame.getWidth() / 2), (screenHeight / 2) - (frame.getHeight() / 2)));
    frame.setVisible(true);
  }

  private int nums;

  // number of nodes
  private ArrayList<Integer> numNodes;
  private MainPanel mainPanel;
  protected ArrayList<BinaryNode> trees;
  protected ArrayList<BinaryNode> hookNodes;

  public PersistentDynamicSet() {
    super();
    nums = 0;
    root = null;
    trees = new ArrayList<>();
    trees.add(root);
    numNodes = new ArrayList<>();
    numNodes.add(0);
    hookNodes = new ArrayList<>();
  }

  // Add the affected nodes to the array
  @Override
  protected void hook(BinaryNode node) {
    hookNodes.add(node);
  }

  //Create new nodes for all affected nodes and connect to all nodes in the previous tree
  @Override
  protected void stopAddHook() {
    System.out.println("add one node--- Affected nodes:"+hookNodes.toString());
    if (trees.get(nums) == null) {
      trees.add(hookNodes.get(0).cloneNode());
      nums++;
      numNodes.add(numNodes.size());
      hookNodes.clear();
      return;
    }
    BinaryNode newNodes = hookNodes.get(0).cloneNode();
    BinaryNode oldNodes = trees.get(nums);
    BinaryNode curNodes = newNodes;
    for (int i = 1; i < hookNodes.size(); i++) {
      BinaryNode hookNode = hookNodes.get(i).cloneNode();
      int compar = compare(hookNode.value, curNodes.value);
      if (compar < 0) {
        curNodes.lChild = hookNode;
        curNodes.rChild = oldNodes.rChild;
        curNodes = curNodes.lChild;
        oldNodes = oldNodes.lChild;
      } 
      else {
        curNodes.rChild = hookNode;
        curNodes.lChild = oldNodes.lChild;
        curNodes = curNodes.rChild;
        oldNodes = oldNodes.rChild;
      }
    }
    trees.add(newNodes);
    hookNodes.clear();
    nums++;
    numNodes.add(numNodes.size());
  }

  //Create new nodes for all affected nodes and connect to all nodes in the previous tree
  @Override
  protected void stopRemoveHook() {
    System.out.println("remove one node--- Affected nodes:"+hookNodes.toString()); 
    BinaryNode newT = hookNodes.get(hookNodes.size() - 1);
    BinaryNode oldT = trees.get(nums);
    if (newT == null) {
      trees.add(null);
      hookNodes.clear();
      nums++;
      numNodes.add(numNodes.get(numNodes.size() - 1) - 1);
      return;
    }   
    Set<E> element = new HashSet<>();
    for (int i = 0; i < hookNodes.size() - 1; i++) 
      element.add(hookNodes.get(i).value);
    BinaryNode Root = newT.cloneNode();
    BinaryNode curNode = Root;
    int t;
    if (newT != null && compare(newT.value, hookNodes.get(0).value) == 0) 
      t = 1;
    else 
      t = 0;
    for (int i = t; i < hookNodes.size() - 1; i++) {
      BinaryNode hookNode = hookNodes.get(i).cloneNode();
      if (newT.lChild != null && compare(newT.lChild.value, hookNode.value) == 0) {
        curNode.lChild = hookNode;
        curNode.rChild = oldT.rChild;
        curNode = curNode.lChild;
        newT = newT.lChild;
        oldT = oldT.lChild;
      } 
      else if (newT.rChild != null && compare(newT.rChild.value, hookNode.value) == 0) {
        curNode.rChild = hookNode;
        curNode.lChild = oldT.lChild;
        curNode = curNode.rChild;
        newT = newT.rChild;
        oldT = oldT.rChild;
      }
    }
    while (curNode != null) {
      boolean flag = true;
      if (newT.lChild != null && element.contains(newT.lChild.value)) {
        curNode.lChild = newT.lChild.cloneNode();
        curNode = curNode.lChild;
        newT = newT.lChild;
        oldT = oldT.lChild;
        flag = false;
      } 
      else if (newT.lChild != null) 
    	curNode.lChild = getNodeFromTree(newT.lChild.value, oldT);
      if (newT.rChild != null && element.contains(newT.rChild.value)) {
        curNode.rChild = newT.rChild.cloneNode();
        curNode = curNode.rChild;
        newT = newT.rChild;
        oldT = oldT.rChild;
        flag = false;
      } 
      else if (newT.rChild != null) 
    	curNode.rChild = getNodeFromTree(newT.rChild.value, oldT);
      if (flag) 
    	break;
    }
    trees.add(Root);
    hookNodes.clear();
    nums++;
    numNodes.add(numNodes.get(numNodes.size() - 1) - 1);
  }

  // Retrieve nodes from the old tree
  private BinaryNode getNodeFromTree(E value, BinaryNode Tree) {
    BinaryNode res;
    int c;
    while ((c = compare(Tree.value, value)) != 0) {
      if (c > 0) 
    	Tree = Tree.lChild;
      else 
    	Tree = Tree.rChild;
    }
    res = Tree;
    return res;
  }

  // GUI
  @SuppressWarnings("serial")
  private class MainPanel extends JPanel {
    private DrawPanel drawPanel;
    private JComboBox<String> treesDropDown;
    private MainPanel(BinaryNode node) {
      super(new BorderLayout());
      super.setPreferredSize(new Dimension(700, 500));
      JPanel buttonPanel = new JPanel();
      treesDropDown = new JComboBox<>();
      updateDrop();
      treesDropDown.addActionListener(
    	  new ActionListener() {
    	    public void actionPerformed(ActionEvent e) {
    		  int index = treesDropDown.getSelectedIndex();
              drawPanel.change(index);
              drawPanel.setElements(numNodes.get(index));
            }
          });
      drawPanel = new DrawPanel(node, numNodes.get(treesDropDown.getSelectedIndex()));
      treesDropDown.setSelectedIndex(nums);
      buttonPanel.add(new JLabel("Binary Search Tree: "));
      buttonPanel.add(treesDropDown);
      super.add(drawPanel, BorderLayout.CENTER);
      super.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateDrop() {
      for (int i = 0; i <= nums; i++) 
    	treesDropDown.addItem("Tree: " + i);
    }
  }

  private void initPanel(BinaryNode node) {
    mainPanel = new MainPanel(node);
  }

  @SuppressWarnings("serial")
  private class DrawPanel extends JPanel {
    private BinaryNode RootNode;
    private int nums;

    public DrawPanel(BinaryNode node, int elements) {
      super();
      RootNode = node;
      this.nums = elements;
      super.setBackground(Color.WHITE);
      super.setPreferredSize(new Dimension(700, 500));
    }

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (RootNode != null) 
    	drawTree(g, getWidth());
    }

    public void drawTree(Graphics g, int width) {
      int nodeCount = -(nums / 2);
      drawNode(g, RootNode, width / 2, 0, nodeCount, new HashMap<>());
    }

    private int drawNode(Graphics g, BinaryNode cur, int x, int level, int nodeCount, Map<BinaryNode, Point> map) {
      int rect = 40;
      if (cur.lChild != null) 
    	nodeCount = drawNode(g, cur.lChild, x, level + 1, nodeCount, map);
      int curX = x + nodeCount * rect;
      int curY = level * 2 * rect + rect;
      nodeCount++;
      map.put(cur, new Point(curX, curY));
      if (cur.rChild != null) 
    	nodeCount = drawNode(g, cur.rChild, x, level + 1, nodeCount, map);
      g.setColor(Color.black);
      if (cur.lChild != null) {
        Point lPoint = map.get(cur.lChild);
        g.drawLine(curX, curY, lPoint.x, lPoint.y - rect / 2);
      }
      if (cur.rChild != null) {
        Point rPoint = map.get(cur.rChild);
        g.drawLine(curX, curY, rPoint.x, rPoint.y - rect / 2);
      }
      g.setColor(Color.green);
      helpDraw(g, cur, map, rect);
      return nodeCount;
    }

    private void change(int index) {
      RootNode = trees.get(index);
      this.repaint();
    }

    public void setElements(int elements) {
      this.nums = elements;
    }
  }
}

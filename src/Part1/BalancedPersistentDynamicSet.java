package Part1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class BalancedPersistentDynamicSet<E> extends BinarySearchTree<E> {
  public static void main(String[] args) {
    BalancedPersistentDynamicSet<String> tree = new BalancedPersistentDynamicSet<>();
    tree.add("age");
    tree.add("job");
    tree.add("dam");
    tree.add("fat");
    tree.add("cap");
    tree.add("god");
    tree.add("egg");
    tree.add("bar");
    tree.add("pig");
    tree.add("nut");
    tree.remove("job");
    tree.remove("cap");
    tree.remove("age");
    tree.remove("dam");
    tree.remove("egg");
    tree.remove("pig");

    JFrame frame = new JFrame("Balanced Persistent Dynamic Set");
    tree.initPanel(tree.trees.get(tree.numVersions));
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

  protected static final Color RED = Color.red;
  protected static final Color BLACK = Color.black;
  @SuppressWarnings("rawtypes")
  protected BalancedNode _node;
  private ArrayList<Integer> numNodes;
  private int numVersions;
  private MainPanel mainPanel;
  @SuppressWarnings("rawtypes")
  protected ArrayList<BalancedNode> trees;
  @SuppressWarnings("rawtypes")
  private HashSet<BalancedNode> hookSet;
  @SuppressWarnings("rawtypes")
  private ArrayList<BalancedNode> hookNodes;
  @SuppressWarnings("rawtypes")
  private BalancedNode addNode;

  @SuppressWarnings("rawtypes")
  protected class BalancedNode<T> extends BinaryNode {
    protected BalancedNode lChild;
    protected BalancedNode rChild;
    protected Color color;
    
    @SuppressWarnings("unchecked")
	public BalancedNode(E value) {
      super(value);
      this.color = Color.RED;
      this.lChild = _node;
      this.rChild = _node;
    }
    
    public String toString(int depth) {
      String res = "";
      if (lChild != _node) 
    	res = res + lChild.toString(depth + 1);
      for (int i = 0; i < depth; i++) 
    	res = res + "  ";
      res = res + toString() + "\n";
      if (rChild != _node) 
    	res = res + rChild.toString(depth + 1);
      return res;
    }

    @SuppressWarnings("unchecked")
	public BalancedNode cloneNode() {
      BalancedNode res = new BalancedNode(value);
      res.color = color;
      return res;
    }
  }

  @SuppressWarnings("serial")
  private class MainPanel extends JPanel {
    private DrawPanel drawPanel;
    private JComboBox<String> treesDrop;

    @SuppressWarnings("rawtypes")
	private MainPanel(BalancedNode node) {
      super(new BorderLayout());
      super.setPreferredSize(new Dimension(700, 500));
      JPanel buttonPanel = new JPanel();
      treesDrop = new JComboBox<>();
      updateDrop();
      treesDrop.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
          int i = treesDrop.getSelectedIndex();
          drawPanel.change(i);
          drawPanel.setElements(numNodes.get(i));
        }
      });
      drawPanel = new DrawPanel(node, numNodes.get(treesDrop.getSelectedIndex()));
      treesDrop.setSelectedIndex(numVersions);
      buttonPanel.add(new JLabel("RedBlack Tree: "));
      buttonPanel.add(treesDrop);
      super.add(drawPanel, BorderLayout.CENTER);
      super.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateDrop() {
      for (int i = 0; i <= numVersions; i++) 
    	treesDrop.addItem("Tree: " + i);
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void set_Node(BalancedNode node) {
    node.color = BLACK;
    _node = node;
    _node.lChild = _node;
    _node.rChild = _node;
    _node.value = null;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public BalancedPersistentDynamicSet() {
    set_Node(new BalancedNode(null));
    root = _node;
    hookSet = new HashSet<>();
    hookNodes = new ArrayList<>();
    trees = new ArrayList<>();
    trees.add((BalancedNode) root);
    numNodes = new ArrayList<>();
    numNodes.add(0);
    hookNodes = new ArrayList<>();
  }

  @SuppressWarnings("rawtypes")
  private void initPanel(BalancedNode node) {
    mainPanel = new MainPanel(node);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected BalancedNode findParent(BalancedNode x) {
    BalancedNode parent = (BalancedNode) root;
    if (x == _node) 
      return _node;
    if (compare((E) x.value, root.value) == 0) 
      return _node;
    while (true) {
      if ((parent.rChild != _node && compare((E) x.value, (E) parent.rChild.value) == 0) || (parent.lChild != _node && compare((E) x.value, (E) parent.lChild.value) == 0)) 
    	break;
      if (compare((E) x.value, (E) parent.value) < 0) 
    	parent = (BalancedNode) parent.lChild;
      else 
    	parent = (BalancedNode) parent.rChild;
    }
    return parent;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void leftRotate(BalancedNode x) {
    BalancedNode y = (BalancedNode) x.rChild;
    
    // Swap the middle subtree from y to x.
    x.rChild = y.lChild;
    if (x.rChild != _node) 
      hookSet.add(x.rChild);
    BalancedNode parent = findParent(x);
    
    // If x is the root of the whole tree, then y is the root.
    if (x == root) 
      root = y;
    else if (x == parent.lChild) 
      parent.lChild = y;
    else 
      parent.rChild = y;

    // Relink x and y.
    y.lChild = x;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void rightRotate(BalancedNode x) {
    BalancedNode y = (BalancedNode) x.lChild;

    // Swap the middle subtree from y to x.
    x.lChild = y.rChild;
    if (x.lChild != _node) 
      hookSet.add(x.lChild);
    BalancedNode parent = (BalancedNode) findParent(x);

    // If x is the root of the whole tree, then y is the root.
    if (x == root) 
      root = y;
    else if (parent.lChild == x) 
      parent.lChild = y;
    else 
      parent.rChild = y;
    
    // Relink x and y.
    y.rChild = x;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public boolean addhelp(E o) {
    BalancedNode newNode = new BalancedNode(o);
    boolean add = false;
    if (root == _node || root == null) {
      root = newNode;
      hook(root);
      add = true;
      addNode = newNode;
    } 
    else { // find where to add new nodes
      BalancedNode curNode = (BalancedNode) root;
      boolean done = false;
      while (!done) {
        hook(curNode);
        int compar = compare(o, (E) curNode.value);
        if (compar < 0)
        {
          if (curNode.lChild == _node) {
            curNode.lChild = newNode;
            done = true;
            add = true;
          } 
          else 
        	curNode = curNode.lChild;
        } 
        else if (compar > 0)
        {
          if (curNode.rChild == _node) {
            curNode.rChild = newNode;
            done = true;
            add = true;
          } 
          else 
        	curNode = curNode.rChild;
        } 
        else if (compar == 0)
          done = true;
      }
    }
    if (add) {
      nums++;
      addNode = newNode;
      if (root != newNode) 
    	hook(newNode);
    }
    return add;
  }

  // Create a new version of the tree
  @SuppressWarnings("rawtypes")
  protected void createNewVersion(BalancedNode node) {
    BalancedNode newT = _node;
    BalancedNode newV = newT;
    if (hookSet.contains(node)) {
      newT = node.cloneNode();
      newV = newT;
    }
    checkAffectedNodes(node, newT);
    trees.add(newV);
    hookSet.clear();
    numVersions++;
    numNodes.add(numNodes.size());
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void checkAffectedNodes(BalancedNode node, BalancedNode newNode) {
    if (hookSet.contains(node.lChild)) {
      BalancedNode t = node.lChild;
      newNode.lChild = node.lChild.cloneNode();
      newNode.lChild.lChild = t.lChild;
      newNode.lChild.rChild = t.rChild;
    } 
    else if (node.lChild != _node) 
      newNode.lChild = getNode((E) node.lChild.value, trees.get(numVersions));
    if (hookSet.contains(node.rChild)) {
      BalancedNode t = node.rChild;
      newNode.rChild = node.rChild.cloneNode();
      newNode.rChild.rChild = t.rChild;
      newNode.rChild.lChild = t.lChild;
    } 
    else if (node.rChild != _node) 
      newNode.rChild = getNode((E) node.rChild.value, trees.get(numVersions));
    if (node != _node) {
      checkAffectedNodes(node.lChild, newNode.lChild);
      checkAffectedNodes(node.rChild, newNode.rChild);
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private BalancedNode getNode(E e, BalancedNode oldT) {
    BalancedNode res = null;
    while (compare((E) oldT.value, e) != 0) {
      int compar = compare((E) oldT.value, e);
      if (compar > 0) 
    	oldT = oldT.lChild;
      else 
    	oldT = oldT.rChild;
    }
    res = oldT;
    return res;
  }

  public boolean add(E element) {
    return addTree(element);
  }

  @SuppressWarnings("rawtypes")
  protected boolean addTree(E e) {
    boolean res = addhelp(e);
    addFixup(addNode);
    hookNodes.clear();
    createNewVersion((BalancedNode) root);
    return res;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void addFixup(BalancedNode y) {
    System.out.println("add one node--- Affected nodes:"+ hookNodes.toString());
    BalancedNode x = null;
    BalancedNode parent = _node;
    BalancedNode grandParent = _node;
    if (hookNodes.size() > 1) 
      parent = hookNodes.get(hookNodes.size() - 2);
    if (hookNodes.size() > 2) 
      grandParent = hookNodes.get(hookNodes.size() - 3);
    while (parent.color == RED) {
      if (parent == grandParent.lChild) {
        x = (BalancedNode) grandParent.rChild;
        if (x.color == RED) {
          parent.color = BLACK;
          x.color = BLACK;
          hookSet.add(x);
          grandParent.color = RED;
          y = grandParent;
          hookNodes.remove(hookNodes.size() - 1);
          hookNodes.remove(hookNodes.size() - 1);
          parent = _node;
          grandParent = _node;
          if (hookNodes.size() > 1) 
        	parent = hookNodes.get(hookNodes.size() - 2);
          if (hookNodes.size() > 2) 
        	grandParent = hookNodes.get(hookNodes.size() - 3);
        } 
        else {
          if (y == parent.rChild) {
            BalancedNode t = y;
            hookNodes.set(hookNodes.size() - 2, y);
            y = parent;
            hookNodes.set(hookNodes.size() - 1, y);
            parent = t;
            leftRotate(y);
          }
          parent.color = BLACK;
          grandParent.color = RED;
          rightRotate(grandParent);
          hookNodes.remove(grandParent);
          grandParent = _node;
          if (hookNodes.size() > 2) 
        	grandParent = hookNodes.get(hookNodes.size() - 3);
        }
      } 
      else {
        x = (BalancedNode) grandParent.lChild;
        if (x.color == RED) {
          parent.color = BLACK;
          x.color = BLACK;
          // Change the color to add to the affected node
          hookSet.add(x);
          grandParent.color = RED;
          y = grandParent;
          parent = _node;
          grandParent = _node;
          if (hookNodes.size() > 1) 
        	parent = hookNodes.get(hookNodes.size() - 2);
          if (hookNodes.size() > 2) 
        	grandParent = hookNodes.get(hookNodes.size() - 3);
        } 
        else {
          if (y == parent.lChild) {
            BalancedNode t = y;
            hookNodes.set(hookNodes.size() - 2, y);
            y = parent;
            hookNodes.set(hookNodes.size() - 1, y);
            parent = t;
            rightRotate(y);
          }
          parent.color = BLACK;
          grandParent.color = RED;
          leftRotate(grandParent);
          hookNodes.remove(grandParent);
          grandParent = _node;
          if (hookNodes.size() > 2) 
        	grandParent = hookNodes.get(hookNodes.size() - 2);
        }
      }
    }
    ((BalancedNode) root).color = BLACK;
  }

  // Restores the red-black property of the tree after deletion.
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void removeFixup(BalancedNode x) {
    BalancedNode parent = findParent(x);
    if (x == _node) 
      parent = hookNodes.get(hookNodes.size() - 1);
    while (x != root && x.color == BLACK) {
      if (parent.lChild == x) {
        BalancedNode y = (BalancedNode) parent.rChild;
        if (y.color == RED) {
          y.color = BLACK;
          hookSet.add(y);
          parent.color = RED;
          leftRotate(parent);
          parent = findParent(x);
          // grandParent = findParent(parent);
          y = parent.rChild;
        }
        if (y.lChild.color == BLACK && y.rChild.color == BLACK) {
          y.color = RED;
          hookSet.add(y);
          x = parent;
          parent = findParent(x);
        } 
        else {
          if (((BalancedNode) y.rChild).color == BLACK) {
            ((BalancedNode) y.lChild).color = BLACK;
            y.color = RED;
            hookSet.add(y);
            rightRotate(y);
            y = parent.rChild;
          }
          y.color = parent.color;
          parent.color = BLACK;
          y.rChild.color = BLACK;
          hookSet.add(y);
          hookSet.add(y.rChild);
          leftRotate(parent);
          x = (BalancedNode) root;
          parent = _node;
        }
      } 
      else {
        BalancedNode y = parent.lChild;
        if (y.color == RED) {
          y.color = BLACK;
          hookSet.add(y);
          parent.color = RED;
          rightRotate(parent);
          parent = findParent(x);
          y = parent.lChild;
        }
        if ((y.rChild).color == BLACK && (y.lChild).color == BLACK) {
          y.color = RED;
          hookSet.add(y);
          x = parent;
          parent = findParent(parent);
        } 
        else {
          if ((y.lChild).color == BLACK) {
            (y.rChild).color = BLACK;
            y.color = RED;
            hookSet.add(y.rChild);
            hookSet.add(y);
            leftRotate(y);
            // parent = findParent(x);
            y = parent.lChild;
          }
          y.color = (parent).color;
          hookSet.add(y);
          (parent).color = BLACK;
          (y.lChild).color = BLACK;
          hookSet.add(y.lChild);
          rightRotate(parent);
          x = (BalancedNode) root;
          parent = _node;
        }
      }
    }
    x.color = BLACK;
    hookNodes.clear();
  }

  // It is a hook method that checks to see if a deleted case needs to be fixed
  @SuppressWarnings({ "rawtypes", "unused" })
  protected void removeNode(BalancedNode removeNode, BalancedNode replacement) {
    System.out.println("remove one node--- Affected nodes:"+hookNodes.toString());
    BalancedNode remove = (BalancedNode) removeNode;
    BalancedNode replace = (BalancedNode) replacement;
    if (replacement != _node) 
      hookSet.add(replacement);
    if (((BalancedNode) removeNode).color == Color.BLACK) 
      removeFixup(replace);
    createNewVersion((BalancedNode) root);
  }

  // Implement a common removal method
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public boolean remove(Object o) {
    boolean remove = false;
    E e = (E) o;
    if (root != _node) {
      if (compare(e, root.value) == 0) {
        remove = true;
        BalancedNode replaceNode = makeReplace ((BalancedNode) root);
        root = replaceNode;
        replaceNode.color = Color.BLACK;
        removeNode((BalancedNode) root, replaceNode);
      } 
      else {
        BalancedNode parentNode = (BalancedNode) root;
        BalancedNode removeNode;
        if (compare(e, root.value) < 0) 
          removeNode = parentNode.lChild;
        else 
          removeNode = parentNode.rChild;
        while (removeNode != _node && !remove) {
          int compar = compare(e, (E) removeNode.value);
          hook(parentNode);
          if (compar == 0) {
            BalancedNode replaceNode;
            if (removeNode == parentNode.lChild) {
              replaceNode = makeReplace(removeNode);
              parentNode.lChild = replaceNode;
            } 
            else {
              replaceNode = makeReplace(removeNode);
              parentNode.rChild = replaceNode;
            }
            removeNode(removeNode, replaceNode);
            remove = true;
          } 
          else {
            parentNode = removeNode;
            if (compar < 0) 
              removeNode = removeNode.lChild;
            else 
              removeNode = removeNode.rChild;
          }
        }
      }
    }
    if (remove) {
      nums--;
      hookSet.clear();
    }
    return remove;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private BalancedNode makeReplace(BalancedNode removeNode) {
    BalancedNode replaceNode = _node;
    // Check to remove a node that has only one child
    if (removeNode.lChild != _node && removeNode.rChild == _node) {
      replaceNode = removeNode.lChild;
      hook(replaceNode);
    } 
    else if (removeNode.lChild == _node && removeNode.rChild != _node) {
      replaceNode = removeNode.rChild;
      hook(replaceNode);
    } 
    else if (removeNode.lChild != _node && removeNode.rChild != _node) {
      BalancedNode parentNode = removeNode;
      replaceNode = removeNode.rChild;
      hook(replaceNode);
      if (replaceNode.lChild == _node) 
    	replaceNode.lChild = removeNode.lChild;
      else {
        do {
          parentNode = replaceNode;
          replaceNode = replaceNode.lChild;
          hook(replaceNode);
        } while (replaceNode.lChild != _node);
        parentNode.lChild = replaceNode.rChild;
        replaceNode.lChild = removeNode.lChild;
        replaceNode.rChild = removeNode.rChild;
      }
    }
    return replaceNode;
  }

  @SuppressWarnings("rawtypes")
  public Object successor(Object node) {
    BalancedNode x = (BalancedNode) node;
    if (x.rChild != _node) 
      return Minimum(x.rChild);
    BalancedNode y = findParent(x);
    while (y != _node && x == y.rChild) {
      x = y;
      y = findParent(y);
    }
    return y;
  }

  @SuppressWarnings("rawtypes")
  protected Object Minimum(BalancedNode x) {
    while (x.lChild != _node) 
      x = x.lChild;
    return x;
  }

  @SuppressWarnings("serial")
  public static class BlackHeightException extends RuntimeException {}

  @SuppressWarnings("rawtypes")
  public int blackHeight(BalancedNode x) {
    if (x == _node) 
      return 0;
    int left = blackHeight((BalancedNode) x.lChild);
    int right = blackHeight((BalancedNode) x.rChild);
    if (left == right) {
      if (x.color == BLACK) 
    	return left + 1;
      else 
    	return left;
    }
    else 
      throw new BlackHeightException();
  }

  @SuppressWarnings("rawtypes")
  public int blackHeight() {
    return blackHeight((BalancedNode) root);
  }

  public String toString() {
    return root.toString(0);
  }

  @SuppressWarnings("rawtypes")
  protected void hook(BinaryNode hookNode) {
    hookSet.add((BalancedNode) hookNode);
    hookNodes.add((BalancedNode) hookNode);
  }

  @SuppressWarnings("serial")
  private class DrawPanel extends JPanel {
    @SuppressWarnings("rawtypes")
	private BalancedNode RootNode;
    private int nums;

    @SuppressWarnings("rawtypes")
	public DrawPanel(BalancedNode node, int nums) {
      super();
      RootNode = node;
      this.nums = nums;
      super.setBackground(Color.WHITE);
      super.setPreferredSize(new Dimension(700, 500));
    }

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (RootNode != null) 
    	drawTree(g, getWidth());
    }

    @SuppressWarnings("rawtypes")
	public void drawTree(Graphics g, int width) {
      int nodeCount = 0 - (nums / 2);
      drawNode(g, RootNode, width / 2, 0, nodeCount, new HashMap<BalancedNode, Point>());
    }

    @SuppressWarnings("rawtypes")
	private int drawNode(Graphics g, BalancedNode cur, int x, int level, int nodeCount, Map<BalancedNode, Point> map) {
      int rect = 40;
      if (cur.lChild != _node) 
    	nodeCount = drawNode(g, cur.lChild, x, level + 1, nodeCount, map);
      int curX = x + nodeCount * rect;
      int curY = level * 2 * rect + rect;
      nodeCount++;
      map.put(cur, new Point(curX, curY));
      if (cur.rChild != _node) 
    	nodeCount = drawNode(g, cur.rChild, x, level + 1, nodeCount, map);
      g.setColor(Color.BLACK);
      if (cur.lChild != _node) {
        Point leftPoint = map.get(cur.lChild);
        g.drawLine(curX, curY, leftPoint.x, leftPoint.y - rect / 2);
      }
      if (cur.rChild != _node) {
        Point rightPoint = map.get(cur.rChild);
        g.drawLine(curX, curY, rightPoint.x, rightPoint.y - rect / 2);
      }
      if (cur.color == Color.BLACK) 
    	g.setColor(Color.DARK_GRAY);
      else if (cur.color == Color.RED) 
    	g.setColor(Color.RED);
      Point curPoint = map.get(cur);
      g.fillRect(curPoint.x - rect / 2, curPoint.y - rect / 2, rect, rect);
      g.setColor(Color.BLACK);
      g.drawRect(curPoint.x - rect / 2, curPoint.y - rect / 2, rect, rect);
      Font f = new Font("Calibri", Font.BOLD, 20);
      g.setFont(f);
      int Width = g.getFontMetrics().stringWidth(cur.toString());
      g.drawString(cur.toString(), curPoint.x - Width / 2, curPoint.y);
      return nodeCount;
    }

    private void change(int index) {
      RootNode = trees.get(index);
      this.repaint();
    }

    public void setElements(int nums) {
      this.nums = nums;
    }
  }
}

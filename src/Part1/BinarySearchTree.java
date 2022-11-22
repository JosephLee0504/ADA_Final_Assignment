package Part1;

import java.awt.*;
import java.util.*;
import javax.swing.*;

public class BinarySearchTree<E> {
  public static void main(String[] args) {
    BinarySearchTree<String> tree = new BinarySearchTree<>();
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

    JFrame frame = new JFrame("Binary Search Tree");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(tree.draw);
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension dimension = toolkit.getScreenSize();
    int screenHeight = dimension.height;
    int screenWidth = dimension.width;
    frame.pack();
    frame.setLocation(new Point((screenWidth / 2) - (frame.getWidth() / 2), (screenHeight / 2) - (frame.getHeight() / 2)));
    frame.setVisible(true);
  }

  protected int nums;
  protected BinaryNode root;
  private DrawPanel draw;
  private Comparator<? super E> compar;

  public BinarySearchTree() {
    this.nums = 0;
    this.draw = new DrawPanel();
  }

  public BinarySearchTree(Comparator<? super E> compar) {
    this();
    this.compar = compar;
  }

  protected int TreeNodes(BinaryNode node) {
    if (node == null) 
      return 0;
    else 
      return TreeNodes(node.lChild) + TreeNodes(node.rChild) + 1;
  }

  //add elements
  public boolean add(E value) {
    BinaryNode node = new BinaryNode(value);
    boolean add = false;
    if (this.root == null) {
      this.root = node;
      hook(node);
      add = true;
    } 
    else {
      BinaryNode curNode = this.root;
      boolean done = false;
      while (!done) {
        hook(curNode);
        int compar = compare(value, curNode.value);
        if (compar < 0) {
          if (curNode.lChild == null) {
            curNode.lChild = node;
            done = true;
            add = true;
          } 
          else 
        	curNode = curNode.lChild;
        } 
        else if (compar > 0) {
          if (curNode.rChild == null) {
            curNode.rChild = node;
            done = true;
            add = true;
          } 
          else 
        	curNode = curNode.rChild;
        } 
        else 
          done = true;
      }
    }
    if (add) {
      this.nums++;
      if (root != node) 
    	hook(node);
      stopAddHook();
    }
    return add;
  }

  //remove elements
  public boolean remove(Object o) {
    Boolean remove = false;
    @SuppressWarnings("unchecked")
	E e = (E) o;
    if (root != null) {
      if (compare(e, root.value) == 0) {
        remove = true;
        root = helpRemove(root);
      } 
      else {
        BinaryNode parent = root;
        BinaryNode removeNode;
        if (compare(e, root.value) < 0) 
          removeNode = root.lChild;
        else 
          removeNode = root.rChild;
        while (removeNode != null && !remove) {
          int compar = compare(e, removeNode.value);
          hook(parent);
          if (compar == 0) {
            if (removeNode == parent.lChild) 
              parent.lChild = helpRemove(removeNode);
            else 
              parent.rChild = helpRemove(removeNode);
            remove = true;
          } 
          else {
            parent = removeNode;
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
      hook(root);
      stopRemoveHook();
    }
    return remove;
  }

  public boolean contains(E value) {
    boolean res = false;
    E e = (E) value;
    BinaryNode cur = root;
    while (!res && cur != null) {
      int com = compare(e, cur.value);
      if (com == 0) 
    	res = true;
      else if (com < 0) 
    	cur = cur.lChild;
      else 
    	cur = cur.rChild;
    }
    return res;
  }

  // help remove
  private BinaryNode helpRemove(BinaryNode node) {
    BinaryNode replace = null;
    if (node.lChild != null && node.rChild == null) {
      replace = node.lChild;
      hook(replace);
    } 
    else if (node.lChild == null && node.rChild != null) {
      replace = node.rChild;
      hook(replace);
    } 
    else if (node.lChild != null) {
      BinaryNode parent;
      replace = node.rChild;
      hook(replace);
      if (replace.lChild == null) 
    	replace.lChild = node.lChild;
      else {
        do {
          parent = replace;
          replace = replace.lChild;
          hook(replace);
        } while (replace.lChild != null);
        parent.lChild = replace.rChild;
        replace.lChild = node.lChild;
        replace.rChild = node.rChild;
      }
    }
    return replace;
  }

  //BinaryTree Node
  protected class BinaryNode {
    public BinaryNode lChild, rChild;
    public E value;
    
    public BinaryNode(E value) {
      this.value = value;
      this.lChild = this.rChild = null;
    }
    
    public String toString() {
      return "" + value;
    }
    
    public String toString(int dep) {
      StringBuilder res = new StringBuilder();
      if (lChild != null) 
    	res.append(lChild.toString(dep + 1));
      for (int i = 0; i < dep; i++) 
    	res.append("  ");
      res.append(toString()).append("\n");
      if (rChild != null) 
    	res.append(rChild.toString(dep + 1));
      return res.toString();
    }
    
    public BinaryNode cloneNode() {
      return new BinaryNode(value);
    }
  }

  private static class Trunk {
    Trunk p;
    String str;

    private Trunk(Trunk p, String str) {
      this.p = p;
      this.str = str;
    }
  }

  @SuppressWarnings("unused")
  private static void showTrunks(Trunk p2) {
    if (p2 == null) 
      return;
    showTrunks(p2.p);
    System.out.print(p2.str);
  }

  //Hook retention method
  protected void hook(BinaryNode node) {}

  protected void stopAddHook() {}

  protected void stopRemoveHook() {}

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected int compare(E e1, E e2) {
    if (compar != null) 
      return compar.compare(e1, e2);
    else if (e1 instanceof Comparable) 
      return ((Comparable) e1).compareTo(e2);
    else if (e2 instanceof Comparable) 
      return -((Comparable) e2).compareTo(e1);
    else 
      return 0;
  }

  @SuppressWarnings("serial")
  private class DrawPanel extends JPanel {
    public DrawPanel() {
      super();
      super.setBackground(Color.WHITE);
      super.setPreferredSize(new Dimension(700, 500));
    }
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (root != null) 
    	drawTree(g, getWidth());
    }
    public void drawTree(Graphics g, int width) {
      int nodeCount = -(nums / 2);
      drawNode(g, root, width / 2, 0, nodeCount, new HashMap<BinaryNode, Point>());
    }
    private int drawNode (Graphics g, BinaryNode cur, int x, int level, int count, Map<BinaryNode, Point> map) {
      int rect = 40;
      if (cur.lChild != null) 
    	count = drawNode(g, cur.lChild, x, level + 1, count, map);
      int curX = x + count * rect;
      int curY = level * 2 * rect + rect;
      count++;
      map.put(cur, new Point(curX, curY));
      if (cur.rChild != null) 
        count = drawNode(g, cur.rChild, x, level + 1, count, map);
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
      return count;
    }
  }

  protected void helpDraw(Graphics g, BinaryNode cur, Map<BinaryNode, Point> map, int rect) {
    Point curPoint = map.get(cur);
    g.fillRect(curPoint.x - rect / 2, curPoint.y - rect / 2, rect, rect);
    g.setColor(Color.BLACK);
    g.drawRect(curPoint.x - rect / 2, curPoint.y - rect / 2, rect, rect);
    Font f = new Font("Calibri", Font.BOLD, 20);
    g.setFont(f);
    int tempWidth = g.getFontMetrics().stringWidth(cur.toString());
    g.drawString(cur.toString(), curPoint.x - tempWidth / 2, curPoint.y);
  }
}

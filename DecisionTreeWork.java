import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.InputMismatchException;

abstract class Node 
{

}

class TreeNode extends Node 
{
	public String treeCharacter;
}

class fileReader
{

	private String input1;

	private String input2;
	
	private String result;
	
	private int NumberIn;

	private String seporator;

	private String[] Elements; 

	public List<String[]> Leaves_Train;

	public List<String[]> Leaves_Test;

	private Set<String> treeMark;

	private Map<String, Set<String>> CharacterLink;

	public fileReader(String input1, String input2, String result, int NumberIn) 
	{
		this.input1 = input1;
		this.input2 = input2;
		this.result = result;
		this.NumberIn = NumberIn;
	}
	
	public int Number_Input() 
	{
		return NumberIn;
	}

	public String[] Element_Input() 
	{
		return Elements;
	}	

	public Set<String> Tree_Marks() 
	{
		return treeMark;
	}
	
	public Map<String, Set<String>> Map_Character() 
	{
		return CharacterLink;
	}

	private String seporateDetector(String line) 
	{
		String symbol = null;
		if(line.indexOf(",") > 0) {
			symbol = ",";
		} else if(line.indexOf(";") > 0) {
			symbol = ";";
		} else if(line.indexOf(" ") > 0) {
			symbol = " ";
		}
		return symbol;
	}
	
	public void input2_file() 
	{
		try 
		{
			File file = new File(input1);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			seporator = seporateDetector(line);

			Elements = line.split(seporator);

			int goalID = Elements.length - 1;
			
			Leaves_Train = new ArrayList<String[]>();
			treeMark = new HashSet<String>();
			CharacterLink = new HashMap<String, Set<String>>();
			while((line = reader.readLine()) != null) 
			{
				if(line.isEmpty() == true) 
				{
					continue;
				}
				
				String[] leaves = line.split(seporator);
				Leaves_Train.add(leaves);

				for(int k = 0; k < goalID; k++) 
				{
					String element = Elements[k];
					String p_v = leaves[k];
					Set<String> p_v_part = CharacterLink.get(element);
					if(p_v_part == null) 
					{
						p_v_part = new HashSet<String>();
						CharacterLink.put(element, p_v_part);
					}
					p_v_part.add(p_v);
				}

				treeMark.add(leaves[goalID]);
			}			
			reader.close();
			System.out.println("Scan Finished");	
		} 
		catch(FileNotFoundException e) 
		{
			System.out.println("Scan error, please re-enter");
			e.printStackTrace();
		} 
		catch(IOException e) 
		{
			e.printStackTrace();
		}		
	}
	
	public void readTestFile() 
	{
		try 
		{
			File file = new File(input2);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			Leaves_Test = new ArrayList<String[]>();
			while((line = reader.readLine()) != null) 
			{
				if(line.isEmpty() == true) 
				{
					continue;
				}				
				String[] leaves = line.split(seporator);
				Leaves_Test.add(leaves);
			}			
			reader.close();
		} 
		catch(FileNotFoundException error) 
		{
			System.out.println("Can't find the file");
			error.printStackTrace();
		} 
		catch(IOException error) 
		{
			error.printStackTrace();
		}		
	}
	
	public void create_doc(String[] outputs) 
	{
		try 
		{
			File file = new File(result);
			BufferedWriter input_w = new BufferedWriter(new FileWriter(file));
			for(int m = 0; m < outputs.length; m++) 
			{
				String[] leaves = Leaves_Test.get(m);

				String leavewords = "";
				for(int n = 0; n < leaves.length; n++) 
				{
					leavewords += leaves[n] + ",";
				}
				input_w.write(leavewords + outputs[m] + "\n");
			}
			input_w.flush();
			input_w.close();
		} 
		catch(IOException e) 
		{
			e.printStackTrace();
		}		
	}	
}

class CreateNode extends Node 
{

	public String element;

	public Node[] nodes;

	public String[] char_value;
}
	
class createTree 
{
	private fileReader content;

	private Node root;

	private List<String> char_Table;

	private Map<String, Set<String>> CharacterLink;

	private Set<String> treeMark;

	private DC_Choice choice; 

	public createTree(fileReader content) 
	{
		this.content = content;		
	}	
	
	public void create() 
	{
		char_Table = new ArrayList<String>();
		for(int i = 0; i < content.Element_Input().length-1; i++) 
		{
			char_Table.add(content.Element_Input()[i]);
		}

		CharacterLink = content.Map_Character();

		treeMark = content.Tree_Marks();

		switch (content.Number_Input()) 
		{
			case 1: 
				choice = new ID3(char_Table, treeMark);
				root = outputTree(content.Leaves_Train, char_Table);
				break;
			case 2:
				choice = new C45(char_Table, treeMark);
				root = outputTree(content.Leaves_Train, char_Table);
				break;
			case 3:
				choice = new CART(char_Table, treeMark, CharacterLink);
				root = split_Tree(content.Leaves_Train, char_Table);
				break;
		}				
	}
	
	private Node outputTree(List<String[]> dataset, List<String> element_table) 
	{
		Map<String, Integer> goal_Freq = choice.frequency_V(dataset, char_Table.size());

		if (goal_Freq.size() == 1) 
		{
			TreeNode treeNode = new TreeNode();
			treeNode.treeCharacter = (String) goal_Freq.keySet().toArray()[0];
			return treeNode;
		}

		if(element_table.isEmpty()) 
		{
			int Count = 0;
			String treeCharacter = "";
			for(Entry<String, Integer> entry : goal_Freq.entrySet()) 
			{
				if(entry.getValue() > Count) 
				{
					Count = entry.getValue();
					treeCharacter = entry.getKey();
				}
			}
			TreeNode treeNode = new TreeNode();
			treeNode.treeCharacter = treeCharacter;
			return treeNode;
		}
		
		String element = choice.elementPart(dataset, element_table, goal_Freq);
		CreateNode DC_Node = new CreateNode();
		DC_Node.element = element;

		List<String> newCharTable = new ArrayList<String>(element_table);
		newCharTable.remove(element);
		int elementID = char_Table.indexOf(element);

		Map<String, List<String[]>> partitions = new HashMap<String, List<String[]>>();
		for(String p_v : CharacterLink.get(element)) 
		{
			partitions.put(p_v, new ArrayList<String[]>());
		}
		for (String[] leaves : dataset) 
		{
			String p_v = leaves[elementID];
			partitions.get(p_v).add(leaves);
		}

		DC_Node.nodes = new Node[partitions.size()];
		DC_Node.char_value = new String[partitions.size()];

		int index = 0;
		for (Entry<String, List<String[]>> partition : partitions.entrySet()) 
		{
			DC_Node.char_value[index] = partition.getKey();
			if(partition.getValue().isEmpty()) 
			{
				int Count = 0;
				String treeCharacter = "";
				for(Entry<String, Integer> entry : goal_Freq.entrySet()) 
				{
					if(entry.getValue() > Count) 
					{
						Count = entry.getValue();
						treeCharacter = entry.getKey();
					}
				}
				TreeNode treeNode = new TreeNode();
				treeNode.treeCharacter = treeCharacter;
				DC_Node.nodes[index] = treeNode;				
			} 
			else 
			{
				DC_Node.nodes[index] = outputTree(partition.getValue(), newCharTable); 				
			}
			index++;
		}

		return DC_Node;		
	}
	
	private Node split_Tree(List<String[]> dataset, List<String> element_table) 
	{
		Map<String, Integer> goal_Freq = choice.frequency_V(dataset, char_Table.size());

		if (goal_Freq.size() == 1) 
		{
			TreeNode treeNode = new TreeNode();
			treeNode.treeCharacter = (String) goal_Freq.keySet().toArray()[0];
			return treeNode;
		}

		if(element_table.isEmpty()) 
		{
			int Count = 0;
			String treeCharacter = "";
			for(Entry<String, Integer> entry : goal_Freq.entrySet()) 
			{
				if(entry.getValue() > Count) 
				{
					Count = entry.getValue();
					treeCharacter = entry.getKey();
				}
			}
			
			TreeNode treeNode = new TreeNode();
			treeNode.treeCharacter = treeCharacter;
			return treeNode;
		}
	
		String element = choice.elementPart(dataset, element_table, goal_Freq);
		List<String> subset = ((CART) choice).getSubsetByAttribute(element);
		Par_DC_Node Par_Node = new Par_DC_Node(); 
		Par_Node.element = element;
		Par_Node.char_value = subset;

		List<String> newCharTable = new ArrayList<String>(element_table);
		newCharTable.remove(element);
		int elementID = char_Table.indexOf(element);

		List<String[]> partitionD1 = new ArrayList<String[]>();
		List<String[]> partitionD2 = new ArrayList<String[]>();
		for (String[] leaves : dataset) 
		{
			String p_v = leaves[elementID];
			if(subset.contains(p_v)) 
			{
				partitionD1.add(leaves);
			} 
			else 
			{
				partitionD2.add(leaves);
			}
		}	
		
		if(partitionD1.isEmpty()) 
		{

			int Count = 0;
			String treeCharacter = "";
			for(Entry<String, Integer> entry : goal_Freq.entrySet()) 
			{
				if(entry.getValue() > Count) 
				{
					Count = entry.getValue();
					treeCharacter = entry.getKey();
				}
			}
			TreeNode treeNode = new TreeNode();
			treeNode.treeCharacter = treeCharacter;
			Par_Node.yesNode = treeNode;	
		} 
		else 
		{
			Par_Node.yesNode = split_Tree(partitionD1, newCharTable); 	
		}
		
		if(partitionD2.isEmpty()) 
		{
			int Count = 0;
			String treeCharacter = "";
			for(Entry<String, Integer> entry : goal_Freq.entrySet()) 
			{
				if(entry.getValue() > Count) 
				{
					Count = entry.getValue();
					treeCharacter = entry.getKey();
				}
			}
			TreeNode treeNode = new TreeNode();
			treeNode.treeCharacter = treeCharacter;
			Par_Node.noNode = treeNode;	
		} 
		else 
		{
			Par_Node.noNode = split_Tree(partitionD2, newCharTable); 	
		}
		
		return Par_Node;		
	}		

	public void print() 
	{
		System.out.println("|--------------Project3 DC Tree--------------|");
		String indent = " ";

		if(content.Number_Input() == 3) 
		{
			print3(root, indent, "");
		}
		else 
		{
			print(root, indent, "");
		}
	}
	
	private void print(Node node, String indent, String p_v) 
	{
		if(p_v.isEmpty() == false) 
		{
			System.out.println(indent + p_v);
		}
		String newIndent = indent + "  ";

		if(node instanceof TreeNode)
		{
			TreeNode treeNode = (TreeNode) node;
			System.out.println(newIndent + " ="+ treeNode.treeCharacter);
		}
		else
		{
			CreateNode DC_Node = (CreateNode) node;
			
			System.out.println(newIndent + DC_Node.element + "->");
			
			newIndent = newIndent + "  ";

			for(int m=0; m< DC_Node.nodes.length; m++)
			{
				print(DC_Node.nodes[m], newIndent, DC_Node.char_value[m]);
			}
		}		
	}
	
	private void print3(Node node, String indent, String branch) 
	{
		if(branch.isEmpty() == false) 
		{
			System.out.println(indent + branch);
		}
		String newIndent = indent + "  ";

		if(node instanceof TreeNode)
		{
			TreeNode treeNode = (TreeNode) node;
		}
		else
		{
			Par_DC_Node Par_Node = (Par_DC_Node) node;
			System.out.println(newIndent + Par_Node.element + Par_Node.char_value.toString() + "->");
			newIndent = newIndent + "  ";

			print3(Par_Node.yesNode, newIndent, "yes");
			print3(Par_Node.noNode, newIndent, "no");
		}		
	}

	public String sortify(String[] leaves) 
	{
		if(content.Number_Input() == 3) 
		{
			return sort_DCtree(root, leaves);
		} 
		else 
		{
			return sort_part_DCtree(root, leaves);
		}
	}
	
	private String sort_part_DCtree(Node node, String[] leaves) 
	{
		if(node instanceof TreeNode) 
		{
			return ((TreeNode) node).treeCharacter;
		} 
		else 
		{
			CreateNode DC_Node = (CreateNode) node;

			int elementID = char_Table.indexOf(DC_Node.element);

			String p_v = leaves[elementID];

			for(int n=0; n< DC_Node.char_value.length; n++)
			{
				if(DC_Node.char_value[n].equals(p_v))
				{
					return sort_part_DCtree(DC_Node.nodes[n], leaves);
				}
			}
		}
		
		return null;
	}	

	private String sort_DCtree(Node node, String[] leaves) 
	{
		if(node instanceof TreeNode) 
		{
			return ((TreeNode) node).treeCharacter;
		} 
		else 
		{
			Par_DC_Node Par_Node = (Par_DC_Node) node;

			int elementID = char_Table.indexOf(Par_Node.element);

			String p_v = leaves[elementID];
			if(Par_Node.char_value.contains(p_v))
			{
				return sort_DCtree(Par_Node.yesNode, leaves);
			} 
			else 
			{
				return sort_DCtree(Par_Node.noNode, leaves);
			}
		}
	}	
}	
	
abstract class DC_Choice 
{
	protected List<String> char_Table;

	protected Set<String> treeMark;

	public DC_Choice(List<String> char_Table, Set<String> treeMark) 
	{
		this.char_Table = char_Table;
		this.treeMark = treeMark;
	}

	abstract public String elementPart(List<String[]> dataset, List<String> element_table, Map<String, Integer> goal_Freq);
	
	public Map<String, Integer> frequency_V(List<String[]> dataset, int elementID) 
	{
		Map<String, Integer> link_Freq = new HashMap<String, Integer>();
		for(String[] leaves : dataset) 
		{
			String p_v = leaves[elementID];
			if(link_Freq.get(p_v) == null) 
			{
				link_Freq.put(p_v, 1);
			} 
			else 
			{
				link_Freq.put(p_v, link_Freq.get(p_v)+1);
			}
		}
		return link_Freq;
	}

	protected double nlog2n(double n) 
	{
		if ( n == 0 )
			return 0;
	    return n * Math.log(n) / Math.log(2);
	}
			
}	
	
class ID3 extends DC_Choice 
{
	public ID3(List<String> char_Table, Set<String> treeMark) 
	{
		super(char_Table, treeMark);
	}

	@Override
	public String elementPart(List<String[]> dataset, List<String> element_table, Map<String, Integer> goal_Freq) 
	{
		double Node_Entropy = 0d;
		for(String p_v : treeMark)  
		{
			Integer fre_v = goal_Freq.get(p_v);
			if(fre_v != null) 
			{
				double p = fre_v / (double) dataset.size();
				Node_Entropy -= nlog2n(p);
			}
		}
		
		System.out.println("Entropy = " + Node_Entropy);		
		String op_Char = "";
		double max_gain_v = Double.NEGATIVE_INFINITY;

		for(String element : element_table) 
		{
			double gain_v = infoGain_V(dataset, element, Node_Entropy);
			System.out.println("\t" + element + "'s gain = " + gain_v);
			if(gain_v > max_gain_v) {
				max_gain_v = gain_v;
				op_Char = element;
			}
		}		
		return op_Char;
	}
	
	private double infoGain_V(List<String[]> dataset, String element, double Node_Entropy)
	{
		int elementID = char_Table.indexOf(element);
		Map<String, Integer> link_Freq_v = frequency_V(dataset, elementID); 

		double info = 0;

		for (Entry<String, Integer> entry : link_Freq_v.entrySet())
		{
			info += entry.getValue() / ((double) dataset.size()) * getEntropy(dataset, elementID, entry.getKey());
		}
		return Node_Entropy - info;
	}
	
	private double getEntropy(List<String[]> dataset, int elementID, String p_v) 
	{
		int leave_n = 0;

		Map<String, Integer> mapTargetFrequency = new HashMap<String, Integer>();
		
		for(String[] leaves : dataset) 
		{
			if(leaves[elementID].equals(p_v)) 
			{
				String targetValue = leaves[char_Table.size()];
				if(mapTargetFrequency.get(targetValue) == null) 
				{
					mapTargetFrequency.put(targetValue, 1);
				} 
				else 
				{
					mapTargetFrequency.put(targetValue, mapTargetFrequency.get(targetValue)+1);
				}
				leave_n++;
			}
		}
		
		double entropy = 0d;
		for(String targetValue : treeMark) 
		{
			Integer fre_v = mapTargetFrequency.get(targetValue);
			if(fre_v != null) 
			{
				double p = fre_v / (double) leave_n;
				entropy -= nlog2n(p);
			}
		}
		return entropy;
	}
}	
	
class C45 extends DC_Choice 
{
	public C45(List<String> char_Table, Set<String> treeMark) 
	{
		super(char_Table, treeMark);
	}

	@Override
	public String elementPart(List<String[]> dataset, List<String> element_table, Map<String, Integer> goal_Freq) {

		double Node_Entropy = 0d;
		for(String p_v : treeMark) 
		{
			Integer fre_v = goal_Freq.get(p_v);
			if(fre_v != null) 
			{
				double p = fre_v / (double) dataset.size(); 
				Node_Entropy -= nlog2n(p);
			}
		}
		
		String op_Char = "";
		double maxRatio = Double.NEGATIVE_INFINITY;

		for(String element : element_table) 
		{
			double gainRatio = getRatio(dataset, element, Node_Entropy);
			System.out.println("\t" + element + "'s Gain-Ratio = " + gainRatio);
			if(gainRatio > maxRatio) 
			{
				maxRatio = gainRatio;
				op_Char = element;
			}
		}		
		return op_Char;
	}

	private double getRatio(List<String[]> dataset, String element, double Node_Entropy) 
	{
		int elementID = char_Table.indexOf(element);
		Map<String, Integer> link_Freq_v = frequency_V(dataset, elementID);
		double info = 0;
		double splitInfo = 0;

		for (Entry<String, Integer> entry : link_Freq_v.entrySet()) 
		{
			double weight = entry.getValue() / ((double) dataset.size());
			info += weight * getEntropy(dataset, elementID, entry.getKey());
			splitInfo -= nlog2n(weight);
		}
		if(splitInfo == 0) 
		{
			return Double.POSITIVE_INFINITY;
		}
		return  (Node_Entropy - info) / splitInfo;
	}
	
	private double getEntropy(List<String[]> dataset, int elementID, String p_v) 
	{
		int leave_n = 0;

		Map<String, Integer> mapTargetFrequency = new HashMap<String, Integer>();
		
		for(String[] leaves : dataset) 
		{
			if(leaves[elementID].equals(p_v)) 
			{
				String targetValue = leaves[char_Table.size()];
				if(mapTargetFrequency.get(targetValue) == null) 
				{
					mapTargetFrequency.put(targetValue, 1);
				} 
				else 
				{
					mapTargetFrequency.put(targetValue, mapTargetFrequency.get(targetValue)+1);
				}
				leave_n++;
			}
		}

		double entropy = 0d;
		for(String targetValue : treeMark)
		{
			Integer fre_v = mapTargetFrequency.get(targetValue);
			if(fre_v != null) 
			{
				double p = fre_v / (double) leave_n;
				entropy -= nlog2n(p);
			}
		}
		return entropy;
	}

}

class CART extends DC_Choice 
{
	private Map<String, Set<String>> CharacterLink;

	private Map<String, List<String>> link_part;

	public CART(List<String> char_Table, Set<String> treeMark, Map<String, Set<String>> CharacterLink)
	{
		super(char_Table, treeMark);
		this.CharacterLink = CharacterLink;
		link_part = new HashMap<String, List<String>>();
	}

	public List<String> getSubsetByAttribute(String element) 
	{
		return link_part.get(element);
	}
	
	@Override
	public String elementPart(List<String[]> dataset, List<String> element_table, Map<String, Integer> goal_Freq) 
	{
		String op_Char = "";
		double minGiniIndex = Double.POSITIVE_INFINITY;

		for(String element : element_table) 
		{
			double giniIndex = Gini_V(dataset, element);
			System.out.println("\t" + element + link_part.get(element).toString() + "'s Gini_index = " + giniIndex);
			if(giniIndex < minGiniIndex) 
			{
				minGiniIndex = giniIndex;
				op_Char = element;
			}
		}		
		return op_Char;
	}

	public static void getSubsets(List<List<String>> allSubsets, List<String> prefix, String[] array, int i) 
	{
		 List<String> subset = new ArrayList<String>();
		 subset.addAll(prefix);
		 subset.add(array[i]);
		 allSubsets.add(subset);
		 i++;
		 if(i < array.length) 
		 {
			 getSubsets(allSubsets, prefix, array, i);
			 getSubsets(allSubsets, subset, array, i);
		 }
	}	

	private double Gini_V(List<String[]> dataset, String element) 
	{
		String[] valueArray = CharacterLink.get(element).toArray(new String[0]);
		List<List<String>> allSubsets = new ArrayList<List<String>>();
		List<String> prefix = new ArrayList<String>();
		getSubsets(allSubsets, prefix, valueArray, 0);
		double minGiniIndex = Double.POSITIVE_INFINITY;
		
		for(int i = 0; i < allSubsets.size(); i++) 
		{
			List<String> subset = allSubsets.get(i);
			if(subset.size() == CharacterLink.get(element).size()) 
			{
				continue;
			}			
			double giniIndex = getGini(dataset, element, subset);
			if(giniIndex < minGiniIndex) 
			{
				minGiniIndex = giniIndex;
				link_part.put(element, subset);
			}
		}
		return minGiniIndex;
	}
	
	private double getGini(List<String[]> dataset, String element, List<String> subset) 
	{
		int elementID = char_Table.indexOf(element);
		int leave1 = 0;
		int leave2 = 0;

		Map<String, Integer> mapTargetFrequencyD1 = new HashMap<String, Integer>();
		Map<String, Integer> mapTargetFrequencyD2 = new HashMap<String, Integer>();
		
		for(String[] leaves : dataset) 
		{
			if(subset.contains(leaves[elementID])) 
			{
				String targetValue = leaves[char_Table.size()];
				if(mapTargetFrequencyD1.get(targetValue) == null) 
				{
					mapTargetFrequencyD1.put(targetValue, 1);
				} 
				else 
				{
					mapTargetFrequencyD1.put(targetValue, mapTargetFrequencyD1.get(targetValue)+1);
				}
				leave1++;
			} 
			else 
			{
				String targetValue = leaves[char_Table.size()];
				if(mapTargetFrequencyD2.get(targetValue) == null) 
				{
					mapTargetFrequencyD2.put(targetValue, 1);
				} 
				else 
				{
					mapTargetFrequencyD2.put(targetValue, mapTargetFrequencyD2.get(targetValue)+1);
				}
				leave2++;
			}
		}

		double giniD1 = 1d;
		for(String targetValue : treeMark) 
		{
			Integer fre_v = mapTargetFrequencyD1.get(targetValue);
			if(fre_v != null) 
			{
				giniD1 -= Math.pow(fre_v / (double) leave1, 2);
			}
		}
		double giniD2 = 1d;
		for(String targetValue : treeMark) 
		{
			Integer fre_v = mapTargetFrequencyD2.get(targetValue);
			if(fre_v != null) 
			{
				giniD2 -= Math.pow(fre_v / (double) leave2, 2);
			}
		}
		double giniIndex = (giniD1 * leave1 + giniD2 * leave2) / ((double) dataset.size());
		return giniIndex;
	}
}
	
class Par_DC_Node extends Node 
{
	public String element;

	public List<String> char_value;

	public Node yesNode;

	public Node noNode;	
}	
	
public class DecisionTreeWork 
{
	public Scanner input;

	private fileReader content;

	private String input1;

	private String input2;

	private String result;

	private createTree dt;

	private int NumberIn;

	private void init() 
	{
		content = new fileReader(input1, input2, result, NumberIn);
	}

	private boolean choice_if(float NumberIn)
	{
		if(NumberIn == 1 || NumberIn == 2 || NumberIn == 3)
			return true;
		else
			return false;
	}		

	public void set() 
	{					
		System.out.println("1.ID3 Algorithm 2.C4.5 Algorithm 3.CART Algorithm\n");
		System.out.print("Choose Algorithm to Run: ");
		boolean isValidID = false;
		try 
		{
			NumberIn = input.nextInt();
			isValidID = choice_if(NumberIn);
		} 
		catch(InputMismatchException e) 
		{
			isValidID = false;
		}
		while(isValidID == false) 
		{
			input.nextLine();
			System.out.println("Invalid input. Please re-enter.");	
			System.out.print("Choose Algorithm to Run: ");	
			try 
			{
				NumberIn = input.nextInt();
				isValidID = choice_if(NumberIn);
			} 
			catch(InputMismatchException e) 
			{
				isValidID = false;
			}
		}

		System.out.print("Training file name: ");
		input1 = input.next();

		boolean isFile = new File(input1).isFile();
		while(isFile == false) 
		{
			System.out.println("Invalid file or file name!");
			System.out.print("Please re-enter training file name: ");		
			input1 = input.next();
			isFile = new File(input1).isFile();
		}		

		System.out.print("Test file name: ");
		input2 = input.next();

		isFile = new File(input2).isFile();
		while(isFile == false) 
		{
			System.out.println("Invalid file or file name!");
			System.out.print("Please re-enter testing file name: ");		
			input2 = input.next();
			isFile = new File(input2).isFile();
		}

		System.out.print("Create a output file name: ");		
		result = input.next();
		
		System.out.print("\n");				

		init();		
	}

	public void train() 
	{
		long startTime = System.currentTimeMillis();

		content.input2_file();

		dt = new createTree(content);
		dt.create();

		long endTime = System.currentTimeMillis();

		dt.print();

		System.out.println("Execution time: " + (endTime-startTime) + "ms\n");
	}
	
	public void test() 
	{

		content.readTestFile();

		String[] outputs = new String[content.Leaves_Test.size()];
		for(int i = 0; i < content.Leaves_Test.size(); i++) 
		{
			outputs[i] = dt.sortify(content.Leaves_Test.get(i));
		}
		content.create_doc(outputs);
	}
	
	public static void main(String[] args) 
	{
		DecisionTreeWork test = new DecisionTreeWork();
		test.input = new Scanner(System.in);

		test.set();
		test.train();
		test.test();
		test.input.close();
		System.exit(-1);
	}

}	
	
	
	
	
	
package aohara.tinkertime;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import aohara.common.tree.TreeNode;
import aohara.common.tree.zip.ZipTreeBuilder;
import aohara.tinkertime.testutil.ModStubs;
import aohara.tinkertime.testutil.ResourceLoader;

public class TestZipTreeBuilder {
	
	private TreeNode get(TreeNode node, String childName){
		TreeNode child = node.getChild(childName);
		assertNotNull(child);
		assertEquals(childName, child.getName());
		return child;
	}
	
	private TreeNode getTree(ModStubs stub) throws IOException{
		Path zipPath = ResourceLoader.getZipPath(stub);
		ZipTreeBuilder builder = new ZipTreeBuilder(zipPath);
		TreeNode root = builder.process();
		
		assertEquals("/", root.getName());
		return root;
	}
	
	@Test
	public void testEngineer() throws IOException {
		TreeNode root = getTree(ModStubs.Engineer);
		
		TreeNode engineer = get(root, "Engineer");
		get(engineer, "Engineer7500");
		get(engineer, "Engineer.dll");
		
		get(root, "Readme.txt");
	}

}

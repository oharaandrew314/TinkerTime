package aohara.tinkertime;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import aohara.common.tree.TreeNode;
import aohara.common.tree.zip.ZipTreeBuilder;
import aohara.tinkertime.testutil.ModStubs;
import aohara.tinkertime.testutil.TestModLoader;

public class TestZipTreeBuilder {
	
	private TreeNode get(TreeNode node, String childName){
		TreeNode child = node.getChild(childName);
		assertEquals(childName, child.getName());
		return child;
	}
	
	private TreeNode getTree(ModStubs stub) throws IOException{
		Path zipPath = TestModLoader.getZipPath(stub.name);
		ZipTreeBuilder builder = new ZipTreeBuilder(zipPath);
		TreeNode root = builder.process();
		
		assertEquals("/", root.getName());
		return root;
	}

	@Test
	public void testMod1() throws IOException {
		TreeNode root = getTree(ModStubs.TestMod1);
		
		TreeNode dependency = get(root, "Dependency");
		get(dependency, "Dependency.txt");
		TreeNode file = get(dependency, "part1.txt");
		assertEquals(Paths.get("Dependency/part1.txt"), file.getPath());
		
		TreeNode testMod1 = get(root, "TestMod1");
		get(get(testMod1, "Icons"), "icon.ico");
		file = get(get(get(get(testMod1, "Parts"), "Fuel"), "BigTank"), "BigTank.tank");
		assertEquals(Paths.get("TestMod1/Parts/Fuel/BigTank/BigTank.tank"), file.getPath());
		get(get(testMod1, "Plugins"), "Foo.dll");
		get(testMod1, "TestMod1.txt");
		
		get(root, "readme.txt");
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

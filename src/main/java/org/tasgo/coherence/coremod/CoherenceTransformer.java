package org.tasgo.coherence.coremod;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CoherenceTransformer implements IClassTransformer, Opcodes { //This is where the transformation magic happens
	public static final Logger logger = LogManager.getLogger("Coherence Transformer");
	
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) { //Forge gives us each class in this function here
        if (transformedName.equals("net.minecraft.client.multiplayer.GuiConnecting")) { //Here we are looking for the right class to modify
        	logger.info("net.minecraft.client.multiplayer.GuiConnecting found. Patching...");
            return transformGuiConnecting(basicClass, transformedName.equals(name)); //If it is found, we then transform it
        }
        else if (transformedName.contains("Logger")) {
        	logger.info(transformedName);
        	try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return basicClass; //Otherwise, return the class as it was
    }
    
    private byte[] transformGuiConnecting(byte[] before, boolean dev) {
        ClassNode classNode = new ClassNode();
        ClassReader reader = new ClassReader(before);
        reader.accept(classNode, 0);
        
        boolean found = false;
        int index;
        String tryingToFind = "<init>";

        for (MethodNode m : classNode.methods) {
            if (m.name.equals(tryingToFind) && m.desc.equals("(Lnet/minecraft/client/gui/GuiScreen;Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/multiplayer/ServerData;)V")) {
                found = true;
                index = getInsertionPoint(m);
                logger.info("Found init function");
                logger.info("Insertion index: " + String.valueOf(index));
                m.instructions.insert(m.instructions.get(index), genInsn());
                break;
            }
        }

        if (!found) {
            System.out.println("Did not find " + tryingToFind + "! Could it have been any of these?");
            for (MethodNode m : classNode.methods) {
                logger.warn("  -" + m.name + "\t" + m.desc);
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        System.out.println("Transformed Minecraft");
        return cw.toByteArray();
    }
    
    private InsnList genInsn() {
    	InsnList instructions = new InsnList();
    	instructions.add(new VarInsnNode(ALOAD, 4));
    	instructions.add(new MethodInsnNode(INVOKESTATIC, "org/tasgo/coherence/client/Client", "ClientInit", "(Lnet/minecraft/client/multiplayer/ServerAddress;)V"));
    	return instructions;
    }
    
    private int getInsertionPoint(MethodNode node) {
    	@SuppressWarnings("unchecked")
    	Iterator<AbstractInsnNode> iter = node.instructions.iterator();
    	AbstractInsnNode currentNode;
    	
    	int index = -1;
    	//Loop over the instruction set and find the ASTORE instruction which does the division of 1/explosionSize
    	while (iter.hasNext())
    	{
    		index++;
    		currentNode = iter.next();

    		//Found it! save the index location of ASTORE instruction and the node for this instruction
    		if (currentNode.getOpcode() == ASTORE)
    			return index;
    	}
    	return -1;
    }
}
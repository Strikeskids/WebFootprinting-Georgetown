import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM4;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class BindIPAgent {
	public static void premain(String args, Instrumentation inst) {
		try {
			inst.appendToBootstrapClassLoaderSearch(new JarFile(new File("boot.jar")));
			System.out.println("Added boot jar");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to add boot jar. Did not add Transformer");
			return;
		}
		System.out.println("Add Transformer");
		inst.addTransformer(new Transformer(), false);

	}

	public static class Transformer implements ClassFileTransformer {
		@Override
		public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
				ProtectionDomain domain, byte[] classfileBuffer) throws IllegalClassFormatException {
			if (className.equals("sun/net/NetworkClient")) {
				System.out.println("Transform sun/net/NetworkClient");

				ClassReader reader = new ClassReader(classfileBuffer);
				ClassWriter out = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
				ConnectTransformer ct = new ConnectTransformer(out);
				reader.accept(ct, 0);
				byte[] transformed = out.toByteArray();
				return transformed;
			}
			return classfileBuffer;
		}
	}

	static class AddBind extends MethodVisitor {
		public AddBind(MethodVisitor mv) {
			super(ASM4, mv);
		}

		@Override
		public void visitLabel(Label label) {
			mv.visitLabel(label);

			if (label.getOffset() == 78) {
				System.out.println("Transform label offset = 78");
				mv.visitVarInsn(ALOAD, 3);
				mv.visitMethodInsn(INVOKESTATIC, "NetworkAddresses", "bindSocket", "(Ljava/net/Socket;)V");
			}

		}
	}

	static class ConnectTransformer extends ClassVisitor {
		public ConnectTransformer(ClassVisitor next) {
			super(ASM4, next);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature,
				String[] exceptions) {
			MethodVisitor ret = cv.visitMethod(access, name, desc, signature, exceptions);
			if (name.equals("doConnect")) {
				System.out.println("Transform doConnect");
				ret = new AddBind(ret);
			}
			return ret;
		}
	}

}

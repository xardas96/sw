package ar.code;

public class CodeDecryptor {
	public static int decryptCode(int[] code){
		int result = 0;
		for(int i = 0; i < code.length; i++){
			result += code[i];
			if(i+1 != code.length)
				result = result << 1;
		}
		return result;
	}
}

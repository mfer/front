package translater;

import java.io.BufferedReader; 
import java.io.FileReader; 
import java.io.IOException; 
import java.util.Scanner;
import java.util.StringTokenizer; 

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import java.io.File;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import java.util.HashMap;
import java.util.Map;

class Tradutor{
    private static Set<String> variaveis = new HashSet<String>(0);
    private static Map<String, String> tipos = new HashMap<String, String>();
    private static Map<String, String> vinculos = new HashMap<String, String>(0);
    
    private static String replaceOperatorsBy(String right, String delimiter){
        right = right.replaceAll("\\s+",""); //replace empty spaces too
        right = right.replace("+", delimiter);
        right = right.replace("-", delimiter);
        right = right.replace("*", delimiter);
        right = right.replace("/", delimiter);
        right = right.replace("(", delimiter);
        right = right.replace(")", delimiter);
        right = right.replace(";", delimiter);
        return right;
    }
    private static void addType( String left, String right ) {
        left = replaceOperatorsBy(left, "^");
        StringTokenizer vtmp = new StringTokenizer(left, "[");
        if(vtmp.countTokens()>=2){
            left = vtmp.nextToken()+"[]";
        }       
        right = replaceOperatorsBy(right, "^");         
        StringTokenizer vars = new StringTokenizer(right, "^");
        while (vars.hasMoreTokens()) {
            String tmp = vars.nextToken();
            String type="";
            if(tmp.contains("true") || tmp.contains("false") ){
                type = "boolean";
            }else if(tmp.contains(".")){
                type = "double";
            }else if(Character.isDigit(tmp.charAt(0))){
                type = "int";
            }else if(tmp.contains("\"")){
                type = "string";
            }else if(tipos.get(tmp) != null){
                type = tipos.get(tmp);
            }else{
                StringTokenizer variable = new StringTokenizer(tmp, "[");
                if(variable.countTokens()>=2){
                    tmp = variable.nextToken()+"[]";                
                    if(tipos.get(tmp) != null){
                        type = tipos.get(tmp);
                    }
                }
            }
            if(!tipos.containsKey(left) && type!=""){
                StringTokenizer variable = new StringTokenizer(left, "[");
                if(variable.countTokens()>=2){
                    left = variable.nextToken()+"[]";
                }
                tipos.put(left, type);
            }else{
                vinculos.put(left, tmp);
            }
        }        
    }
    private static void addL( String left ) {
        left = left.replaceAll("\\s+","");
        if(!Character.isDigit(left.charAt(0))){
            StringTokenizer variable = new StringTokenizer(left, "[");
            if(variable.countTokens()>=2){
                left = variable.nextToken()+"[]";
            }            
            if(!left.contains("true") && !left.contains("false")){
                variaveis.add(left);
            }
        }
    }
    private static void addR( String right ) {
        right = replaceOperatorsBy(right, "^");
        StringTokenizer vars = new StringTokenizer(right, "^");
        while (vars.hasMoreTokens()) {
            String tmp = vars.nextToken();              
            addL(tmp);
        }
    }
    public static void main(String[] args) {
		String nome = args[0];
		File f = new File(nome);
		String nomeArquivo=f.getName().substring(0, f.getName().indexOf('.'));
		String nomeClasse = nomeArquivo.substring(0, 1).toUpperCase() + nomeArquivo.substring(1);
		try {
			File file = new File(nomeArquivo+".java");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			int bucha=0;
			bw.write("package tmp;\n");       
			bw.write("class "+nomeClasse+"{\n");
			bw.write("public static void main(String[] args) {\n");
			bw.write("boolean stop=false;\n");
			bw.write("while(!stop){\n");
			bw.write("switch(gotoL){\n");
			bw.write("case "+bucha+":\n");
			FileReader arq = new FileReader(nome); 
			BufferedReader lerArq = new BufferedReader(arq); 
			String linha = lerArq.readLine(); 
			while (linha != null) { 
				StringTokenizer label = new StringTokenizer(linha, ":");
				if(label.countTokens()>=2){
					for(int i=1; i <= label.countTokens(); i++)
					{
						bw.write("case "+label.nextToken().substring(1)+":\n");
					}
				}
				if( linha.substring(linha.length() - 1).contains(":")){
					int i = linha.lastIndexOf('L'); 
					String buffer = linha.substring(i+1); 
					bw.write("case "+buffer+"\n");
					bw.write("stop = true;\n");
				}
				String quadrupla = label.nextToken();
				if (quadrupla.toLowerCase().contains("iffalse")){
					StringTokenizer iffalse = new StringTokenizer(quadrupla, " ");
					if(iffalse.countTokens()>=2){
						iffalse.nextToken(); //to jump iffalse
						String A = iffalse.nextToken();
						String test = iffalse.nextToken();
						if (!test.contains("goto")) {
							String op = test;
							String B = iffalse.nextToken();
							iffalse.nextToken(); //to jump goto
							String L = iffalse.nextToken();
							bw.write("if ( !("+A+' '+op+' '+B+") ) {\n gotoL = "+L.substring(1)+";\n break;\n }\n");
							addR(A);
							addR(B);
							addType(A, B);
						}else{
							String L = iffalse.nextToken();
							bw.write("if ( !("+A+") ) {\n gotoL = "+L.substring(1)+";\n break;\n }\n");
							addR(A);
							addType(A, "true");//forçando para que seja boolean
						}
					}
				}else if (quadrupla.toLowerCase().contains("if")){
					StringTokenizer iff = new StringTokenizer(quadrupla, " ");
					if(iff.countTokens()>=2){
						iff.nextToken(); //to jump if
						String A = iff.nextToken();
						String test = iff.nextToken();
						if (!test.contains("goto")) {
							String op = test;
							String B = iff.nextToken();
							iff.nextToken(); //to jump goto
							String L = iff.nextToken();
							bw.write("if ( "+A+' '+op+' '+B+" ) {\n gotoL = "+L.substring(1)+";\n break;\n }\n");
							addR(A);
							addR(B);
							addType(A, B);
						}else{
							String L = iff.nextToken();
							bw.write("if ( "+A+" ) {\n gotoL = "+L.substring(1)+";\n break;\n }\n");
							addR(A);
							addType(A, "true");//forçando para que seja boolean
						}
					}
				}else if (quadrupla.toLowerCase().contains("goto")){
					StringTokenizer gotoo = new StringTokenizer(quadrupla, "L");
					if(gotoo.countTokens()>=2){
						gotoo.nextToken(); //to jump goto
						String L = gotoo.nextToken();
						if(linha.contains(":")){
							bw.write("gotoL = "+L+";\n break;\n");
						}else{
							bw.write("case "+--bucha+":\n gotoL = "+L+";\n break;\n");
						}
					}
				}else{
					StringTokenizer atrib = new StringTokenizer(quadrupla, "=");
					if(atrib.countTokens()>=2){
						String left = atrib.nextToken();
						String right = atrib.nextToken();
						addL(left);
						addType(left, right);
						addR(right);
						if(tipos.get(left.replaceAll("\\s+","")) == tipos.get(right.replaceAll("\\s+","")) 
							|| tipos.get(left.replaceAll("\\s+","")) == null
							|| tipos.get(right.replaceAll("\\s+","")) == null){
							bw.write(quadrupla.replaceAll("\\s+","")+";\n");
						}else{
							bw.write(quadrupla.replaceAll("\\s+","").replaceAll("=","=("+tipos.get(left.replaceAll("\\s+",""))+")")+";\n");
						}
					}else{
					}
				}
				linha = lerArq.readLine(); 
			}
			arq.close(); 
			bw.write("}\n"); //close switch
			bw.write("}\n"); //close while
			bw.write("}\n"); //close main
			bw.write("public static int gotoL=1;\n");
			Iterator<String> it = variaveis.iterator();
			while (it.hasNext()) {
				StringTokenizer variable = new StringTokenizer(it.next(), "[");
				if(variable.countTokens()<2){
					String key = variable.nextToken();
					String value = tipos.get(key);
					if (value == null){
						Iterator<String> keySetIterator = vinculos.keySet().iterator();
						while(keySetIterator.hasNext()){
						  String k = keySetIterator.next();
						  if ( k.contains(key) ) {
							  value = tipos.get(vinculos.get(k));
						  }else if ( vinculos.get(k).contains(key) ) {
							  value = tipos.get(k);
						  }                      
						}
						
					}
					if (value == null){ value="int";}
					bw.write("public static "+value+" "+key+";\n");
				}else{
					String key = variable.nextToken();
					String value = tipos.get(key+"[]");
					if (value == null) value = "int";
					bw.write("public static "+value+" "+key+"[] = new "+value+"[10000];\n");
				}
			}
			bw.write("}\n"); //close class
			bw.close();
		} catch (IOException e) { 
			System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage()); 
		} 
    }
}

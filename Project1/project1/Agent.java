package project1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures:
 * public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     * 
     * Do not add any variables to this signature; they will not be used by
     * main().
     * 
     */
    HashMap<String,String> transformations = new HashMap<>();
    HashMap<String,String> attributes = new HashMap<>();
    HashMap<String,Double> transformationScore = new HashMap<>();
    
    public Agent() {
        transformations.put("scaled","size");
        transformations.put("angle","angle");
        transformations.put("fill","fill");
        transformations.put("location","above,inside,under,below,left,right,left-of,right-of");
        
        attributes.put("fill","fill");
        
        transformationScore.put("scaled", 1.0);
        transformationScore.put("angle", 1.0);
        transformationScore.put("fill", 1.0);
        transformationScore.put("location", 1.0);
    }
    /**
     * The primary method for solving incoming Raven's Progressive Matrices.
     * For each problem, your Agent's Solve() method will be called. At the
     * conclusion of Solve(), your Agent should return a String representing its
     * answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
     * are also the Names of the individual RavensFigures, obtained through
     * RavensFigure.getName().
     * 
     * In addition to returning your answer at the end of the method, your Agent
     * may also call problem.checkAnswer(String givenAnswer). The parameter
     * passed to checkAnswer should be your Agent's current guess for the
     * problem; checkAnswer will return the correct answer to the problem. This
     * allows your Agent to check its answer. Note, however, that after your
     * agent has called checkAnswer, it will *not* be able to change its answer.
     * checkAnswer is used to allow your Agent to learn from its incorrect
     * answers; however, your Agent cannot change the answer to a question it
     * has already answered.
     * 
     * If your Agent calls checkAnswer during execution of Solve, the answer it
     * returns will be ignored; otherwise, the answer returned at the end of
     * Solve will be taken as your Agent's answer to this problem.
     * 
     * @param problem the RavensProblem your agent should solve
     * @return your Agent's answer to this problem
     */
    public String Solve(RavensProblem problem) {
        String answer="1";
        println();
        println("---------------PROBLEM:" + problem.getName() +"----------------");
        println();
        RavensFigure objA = problem.getFigures().get("A");
        RavensFigure objB = problem.getFigures().get("B");
        RavensFigure objC = problem.getFigures().get("C");
        RavensFigure obj1 = problem.getFigures().get("1");
        RavensFigure obj2 = problem.getFigures().get("2");
        RavensFigure obj3 = problem.getFigures().get("3");
        RavensFigure obj4 = problem.getFigures().get("4");
        RavensFigure obj5 = problem.getFigures().get("5");
        RavensFigure obj6 = problem.getFigures().get("6");

        String debugProblem = "2x1 Basic Problem 11";
        if (problem.getName().equals(debugProblem)){
        
        //-- Stage 1
        HashMap<String,String> ObjectMapping = VerifyCorrelation(objA,objB);
        HashMap<String, String> ab = BuildComparisonSheet(objA, objB, new HashMap<String,String>());
        HashMap<String, String> c1 = BuildComparisonSheet(objC, obj1, ObjectMapping);
        HashMap<String, String> c2 = BuildComparisonSheet(objC, obj2, ObjectMapping);
        HashMap<String, String> c3 = BuildComparisonSheet(objC, obj3, ObjectMapping);
        HashMap<String, String> c4 = BuildComparisonSheet(objC, obj4, ObjectMapping);
        HashMap<String, String> c5 = BuildComparisonSheet(objC, obj5, ObjectMapping);
        HashMap<String, String> c6 = BuildComparisonSheet(objC, obj6, ObjectMapping);

        int[] score = {0,0,0,0,0,0,0};
        
        //--Stage 2 & 3
        score[1]+=ScoreFactSheets(ab, c1, "1");
        score[2]+=ScoreFactSheets(ab, c2, "2");
        score[3]+=ScoreFactSheets(ab, c3, "3");
        score[4]+=ScoreFactSheets(ab, c4, "4");
        score[5]+=ScoreFactSheets(ab, c5, "5");
        score[6]+=ScoreFactSheets(ab, c6, "6");
        
        //--Stage 4
        for(int i=0;i<score.length;i++){
            println(i+"=>"+score[i]);
        }
        
        int max = score[0];
        int maxI = 0;
        for ( int i = 1; i < score.length; i++) {
            if ( score[i] > max) {
              max = score[i];
              maxI = i;
            }
        }
        int correct=0;
        int wrong=0;
        answer = String.valueOf(maxI);
        println("Answer: "+answer);
        }//For debugging purposes
        String correctAnswer = problem.checkAnswer(answer);
        System.out.println("The correct answer is: "+ correctAnswer);
        System.out.println("Robbie guessed: "+ answer);
        return String.valueOf(answer);

    }
    
    private  void println()
    {
        println("");
    }
    private void println(String text)
    {
        System.out.println(text);
    }
    
    //--Used to score the transformation knowledge sheets
    private double ScoreFactSheets(HashMap<String, String> c, HashMap<String, String> o, String num)
    {
    	String debug = "Verbose";
    	
        double score = 0;
        HashSet<String> ShapesInC = new HashSet<>();
        int shapesInCQty = 0;
        int countChanged = Integer.valueOf(c.containsKey("count_changed")?c.get("count_changed"):"0");
        
        for(String key : c.keySet())
        {
            
            //match keys:values from both transformation sheets without matching shape
            
            //String t[] = key.split("\\.");
            String objKey = (key.split("\\.")[0].equals("A"))?"C":num;
            String exactKey = key.replaceFirst("\\w\\.", "");
            String cKey = exactKey.contains(".")?objKey+"."+exactKey:exactKey;
            String val1 = c.get(key);
            String val2 = (o.containsKey(cKey))?o.get(cKey):null;
            println(key+" : "+val1+" - "+cKey+" : "+val2);
            if(!key.contains("shape"))
            {
                score+=(val1.equals(val2))?1:0;
                if(debug.equals("Verbose"))System.out.println("*****Score increased******");
                
            }
            else 
            {
                if (cKey.contains("C"))
                {
                    ShapesInC.add(val2);
                    shapesInCQty++;
                }
            }
        }
        
        //--tie brakers galore --------------------
        int shapesInAnswer=0;
        int expectedCountOfNewObjects = 0;
        if(c.containsKey("tf-shpe_added"))
            expectedCountOfNewObjects = shapesInCQty + Math.abs(countChanged);
        else if (c.containsKey("tf-shpe_deleted"))
            expectedCountOfNewObjects = shapesInCQty - Math.abs(countChanged);
       
        System.out.println("Expected Objects: "+expectedCountOfNewObjects);
        for(String shape:ShapesInC)
        {
            shapesInAnswer=0;
            for(String key : o.keySet())
            {
                //Most objects from C should be in answer except if shape changed from A -> B
                if(key.contains("shape") && key.contains(num) && o.get(key).equals(shape) && !c.containsKey("tf-shpe_changed") ){
                    score++;
                    if(debug.equals("Verbose"))System.out.println("*****Score increased******");
                }
                
                
                if(key.contains("shape") && key.contains(num))
                    shapesInAnswer++;
            }
        }
        
        //Amount of objects in answer should match qty objects in C less deleted objects
        if(shapesInAnswer == expectedCountOfNewObjects)
            score++;
        
        System.out.println();
        
        return score;
    
    }
    
    //--Print transformation sheet
    private void PrintSheet(HashMap<String, String> sheet)
    {
        for (Entry<String, String> entry : sheet.entrySet()) 
            System.out.println(entry.getKey() + ":" + entry.getValue());
    }
    
    private void AreAllShapesSame(HashMap<String, String> ret1, HashMap<String, String> ret2, HashMap<String, String> ret) {
        boolean shapeSame = true;
        String shape = "";
        String shapeRet1 = "";
        String shapeRet2 = "";
        
        for(String entry : ret1.keySet())
        {
            if (entry.toLowerCase().contains("shape"))
            {
                if (shapeRet1.equals("") && shapeSame){
                    shapeRet1 = ret1.get(entry);
                } else if (!shapeRet1.equals(ret1.get(entry))){
                    shapeSame = false;
                }
            }
        }
        ret.put("tf-same_shpe_fig1", String.valueOf(shapeSame));
        shapeSame = true;
        for(String entry : ret2.keySet())
        {
            if (entry.toLowerCase().contains("shape"))
            {
                if (shapeRet2.equals("") && shapeSame){
                    shapeRet2 = ret2.get(entry);
                } else if (!shapeRet1.equals(ret2.get(entry))){
                    shapeSame = false;
                }
            }
        }
        ret.put("tf-same_shpe_fig2", String.valueOf(shapeSame));
    }
    
    private void DidShapeChange(HashMap<String,String> ret1, HashMap<String,String> ret2, HashMap<String,String> ret)
    {
        boolean shapeChanged = false;
        int cnt = 0;
        for(String entry : ret1.keySet())
        {
            if (entry.toLowerCase().contains("shape"))
            {
                String shapeRet1 = ret1.get(entry);
                String shapeRet2 = (ret1.containsKey(entry)) ?ret2.get(entry):"";
                if(!shapeRet1.equals(shapeRet2)){
                    shapeChanged = true;
                    cnt++;
                }
            }
        }
        
        if(shapeChanged)
            ret.put("tf-shpe_changed", String.valueOf(cnt));
    }
    
    //--Build transformation sheets
    private HashMap<String,String> BuildComparisonSheet(RavensFigure figure1, RavensFigure figure2, HashMap<String,String> ObjectMapping)
    {
        HashMap<String,String> ret = new HashMap<>();
        HashMap<String,String> ret1 = new HashMap<>();
        HashMap<String,String> ret2 = new HashMap<>();
        
        HashMap<String,String> realObjectMappingFig1 = new HashMap<>();
        if(!ObjectMapping.isEmpty() ){
            for(Entry<String,String> entry : ObjectMapping.entrySet()){
                String size = entry.getValue();
                for(RavensObject obj:figure1.getObjects())
                {
                    for(RavensAttribute att :  obj.getAttributes()){
                        if(att.getName().toLowerCase().contains("size") && att.getValue().equals(size)){
                            realObjectMappingFig1.put(obj.getName(), entry.getKey().split("\\.")[0]);
                        }
                    }

                }
            }
        }
        HashMap<String,String> realObjectMappingFig2 = new HashMap<>();
        if(!ObjectMapping.isEmpty() ){
            for(Entry<String,String> entry : ObjectMapping.entrySet()){
                String size = entry.getValue();
                for(RavensObject obj:figure2.getObjects())
                {
                    for(RavensAttribute att :  obj.getAttributes()){
                        if(att.getName().toLowerCase().contains("size") && att.getValue().equals(size)){
                            realObjectMappingFig2.put(obj.getName(), entry.getKey().split("\\.")[0]);
                        }
                    }

                }
            }
        }
        
        
        ExtractAttributes(figure1, ret1, realObjectMappingFig1);
        ExtractAttributes(figure2, ret2, realObjectMappingFig2);
        
        
        int cnt = figure2.getObjects().size() - figure1.getObjects().size();
        
        //Determine if shapes were added or removed
        if(figure2.getObjects().size() > figure1.getObjects().size())
            ret.put("tf-shpe_added", "1");
        else if(figure2.getObjects().size() < figure1.getObjects().size())
            ret.put("tf-shpe_deleted", "1");
        
        DidShapeChange(ret1, ret2, ret);
        AreAllShapesSame(ret1, ret2, ret);
        
        for(Entry<String,String> entry: transformations.entrySet())
        {
            String prevEntry = null;
            String transform = null;
            String key = entry.getKey();
            String value = entry.getValue();
            String[] tags = entry.getValue().split(",");
            
            for (String tag : tags) {
                for(Entry<String,String> retEntry : ret1.entrySet())
                {
                    //If they both contain the same tag
                    if(retEntry.getKey().trim().toLowerCase().contains(tag.toLowerCase()) && ret2.containsKey( retEntry.getKey()))
                    {
                        if(!retEntry.getValue().equals(ret2.get(retEntry.getKey())))
                        {
                            transform = entry.getKey();
                            //transformValue = retEntry.getValue();
                            if(attributes.containsKey(transform))
                                ret.put(transform, ret2.get(retEntry.getKey()) );
                            else
                                ret.put("tf-"+transform, transform); //TODO: New frame needs to be created also
                            transform = null;
                        }
                    }
                }
                
            }
        }
        
        for(Entry<String,String> entry : ret1.entrySet())
        {
            ret.put(figure1.getName()+"."+entry.getKey(), entry.getValue());
        }
        for(Entry<String,String> entry : ret2.entrySet())
        {
            ret.put(figure2.getName()+"."+entry.getKey(), entry.getValue());
        }
        ret.put("count_changed", ""+cnt);

        return ret;
    }

    private void ExtractAttributes(RavensFigure figure, HashMap<String, String> ret, HashMap<String,String> realObjectMapping) {
        for(RavensObject obj:figure.getObjects())
        {
            String objName = obj.getName();
            for(RavensAttribute att:obj.getAttributes())
            {
                //ret.put(figure.getName()+"."+obj.getName()+"."+att.getName(), att.getValue());
                String realName = (!realObjectMapping.isEmpty() && realObjectMapping.containsKey(obj.getName()))? realObjectMapping.get(obj.getName()):obj.getName();
                ret.put(realName+"."+att.getName(), att.getValue());
            }
        }
    }
    
    private HashMap<String,String> VerifyCorrelation(RavensFigure figure1, RavensFigure figure2) {
        HashMap<String,String> corrFig1 = new HashMap<>();
        HashMap<String,String> corrFig2 = new HashMap<>();
        HashMap<String,String> retCorrelation = new HashMap<>();
        boolean correlated = true;
        
        for(RavensObject obj:figure1.getObjects())
        {
            for(RavensAttribute att :  obj.getAttributes()){
                if(att.getName().toLowerCase().contains("shape") || att.getName().toLowerCase().contains("size")){
                    corrFig1.put(obj.getName()+"."+att.getName(), att.getValue());
                    
                }
            }
            
        }
        for(RavensObject obj:figure2.getObjects())
        {
            for(RavensAttribute att :  obj.getAttributes()){
                if(att.getName().toLowerCase().contains("shape") || att.getName().toLowerCase().contains("size")){
                    corrFig2.put(obj.getName()+"."+att.getName(), att.getValue());
                }
            }
            
        }
        
         
        //Test correlations
        for(Entry<String,String> entry : corrFig1.entrySet()){
            if(corrFig2.containsKey(entry.getKey()) && !entry.getValue().equals(corrFig2.get(entry.getKey())))
                correlated = false;
        }
        //HashMap<String, String> ab = BuildComparisonSheet( corrFig1, corrFig2);
        if(correlated){
            //ret.put("tf-correlated", "yes");
            for(Entry<String,String> entry : corrFig1.entrySet()){
                if(entry.getKey().toLowerCase().contains("size"))
                    retCorrelation.put(entry.getKey(), entry.getValue());
            }
        }
        int d=0;
        return retCorrelation;
    }
    
    private HashSet<RavensAttribute> FindDifference(RavensObject A, RavensObject B)
    {
        HashSet<RavensAttribute> ret = new HashSet<RavensAttribute>();
        for(RavensAttribute ravensAttributeA : A.getAttributes())
        {
            for(RavensAttribute ravensAttributeB : B.getAttributes())
            {
                if (ravensAttributeA.getName() == null ? ravensAttributeB.getName() == null : ravensAttributeA.getName().equals(ravensAttributeB.getName()))
                    if(ravensAttributeA.getValue() == null ? ravensAttributeB.getValue() != null : !ravensAttributeA.getValue().equals(ravensAttributeB.getValue()))
                        ret.add(new RavensAttribute(ravensAttributeA.getName(), ravensAttributeB.getValue()));
            }
                
        }
        
        return ret;
    }

    

    
}
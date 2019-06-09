import com.sun.org.apache.xpath.internal.functions.FuncSubstring;
import sun.print.SunPageSelection;

import java.util.ArrayList;
import java.util.Scanner;
public class main {
    public static void main (String[] args)
    {
        String Rel=new String();
        int pkcount=0;
        char a;

        //Getting Relationship
        System.out.println("Please Insert The Relationship: ");
        Scanner Input=new Scanner(System.in);
        Rel=Input.next();

        //Getting Primary Key(s)
        System.out.print("Please Enter The Number Of Primary Keys:");
        pkcount=Input.nextInt();
        System.out.println("Now Enter The Primary Key(s):");
        String[] primarykeys=new String[pkcount];
        for(int j=0;j<pkcount;j++)
        {
            System.out.print("P.K. "+(j+1)+": ");
            primarykeys[j]=Input.next();
        }

        //Divide Relationship String To Name & Attributes
        String[] AttArray=new String [20];
        int[] cutchars=new int[20];
        String relname;

        //Getting Index Of Cutter Characters
        cutchars[0]=Rel.indexOf("(",0);
        relname=Rel.substring(0,cutchars[0]);
        int ccc=1; //ccc=Cutter Characters Counter
        while(true)
        {
            if(Rel.substring(cutchars[ccc-1]+1).contains(","))
            {
                cutchars[ccc] = Rel.indexOf(",", cutchars[ccc - 1]+1);
                ccc++;
            }
            else
                break;
        }
        cutchars[ccc]=Rel.indexOf(")",cutchars[ccc-1]+1);

        //Cutting Relationship String To Attributes
        int ac=0; //ac=Attributes Counter
        while(ac<ccc)
        {
            AttArray[ac]= Rel.substring(cutchars[ac]+1,cutchars[ac+1]);
            ac++;
        }

        //Getting Functional Associations
        String[][] fa=new String[2][20];
        boolean question=true,flag=true;
        int facount = 0;
        int fac = 0; //Functional Associations Counter
        System.out.println("\nPlease Insert Functional Associations:");
        do {
            flag=true;
            fa[0][fac]=Input.next();
            System.out.print("with: ");
            fa[1][fac]=Input.next();
            fac++;
            facount++;
            while (flag)
            {
                System.out.println("Do You Have Anymore Functional Association? (y/n)");
                String answer=Input.next();
                a = answer.charAt(0);
                if (a == 'n'||a=='N')
                {
                    question = false;
                    flag = false;
                    System.out.println("(Your Data Entered Completely)");
                }
                else if (a == 'y'||a=='Y')
                {
                    question = true;
                    flag = false;
                    System.out.println("Please Enter One More:");
                }
                else
                    System.out.println("Wrong Input!!!");
            }
        } while (question);

        //Checking For 1NF
        boolean NF1=true;
        for (int i=0;i<ac;i++)
            for(int j=0;j<ac;j++)
                if (AttArray[i].equals(AttArray[j]) && i!=j)
                {
                    NF1=false;
                    for (int k=j;k<=ac;k++)
                        AttArray[k]=AttArray[k+1];
                    ac--;
                }

        //Change & Print The Relationship To 1NF
        if(!NF1)
        {
            System.out.println("Your Relationship Is Not 1NF!\nYour 1NF Relationship Is:");
            System.out.print(relname+"(");
            for(int i=0;i<ac;i++)
            {
                System.out.print(AttArray[i]);
                if(i<ac-1)
                    System.out.print(",");

            }
            System.out.print(")\n");
            NF1=true;
        }
        else
            System.out.println("Your Relationship Is Already 1NF!");

        //Checking For 2NF
        boolean NF2=true;
        int[] fapkcount=new int[facount];
        if (pkcount>1)
        {
            for(int i=0;i<facount;i++)
            {
                int fapk=1;
                if(fa[0][i].contains(","))
                {
                    int index=0;
                    String substring=fa[0][i];
                    while(substring.contains(","))
                    {
                        index=substring.indexOf(",",index+1);
                        substring=fa[0][i].substring(index+1);
                        fapk++;
                    }
                    fapkcount[i]=fapk;
                }
                else
                    fapkcount[i]=fapk;
            }
            for(int i=0;i<facount;i++)
            {
                if(facount>1 && fapkcount[i]!=fapkcount[i+1])
                {
                    NF2=false;
                    System.out.println("Your Relationship Is Not 2NF!");
                    break;
                }
            }
            if(NF2&&NF1)
                System.out.println("Your Relationship Is Already 2NF!");
        }
        else
            System.out.println("Your Relationship Is Already 2NF!");

        //Change The Relationship To 2NF
        ArrayList<String> pks=new ArrayList<>();
        ArrayList<ArrayList<String>> resArray=new ArrayList<>();
        boolean ispk=false;
        boolean[] aflag=new boolean[ac];
        for(int i=0;i<ac;i++)
            aflag[i]=false;
        if(!NF2)
        {
            for(int i=0;i<facount;i++)
                pks.add(fa[0][i]);
            for (int i=0;i<pks.size();i++)
                for(int j=0;j<pks.size();j++)
                    if(pks.get(i).equals(pks.get(j))&&i!=j)
                        pks.remove(j);
            for (int i=pks.size()-1;i>=0;i--)
            {
                ispk=false;
                for (int j = 0; j < pkcount; j++)
                    if (pks.get(i).equals(primarykeys[j]))
                        ispk = true;
                    else if(pks.get(i).contains(","))
                    {
                        ispk=true;
                        int index=0;
                        String substring1=pks.get(i);
                        String substring2=pks.get(i);
                        while(substring2.contains(",")&&ispk)
                        {
                            ispk=false;
                            index = substring2.indexOf(",", index + 1);
                            substring1 = substring2.substring(0,index);
                            substring2=substring2.substring(index + 1);
                            for(int k=0;k<pkcount;k++)
                                if(substring1.equals(primarykeys[k]))
                                    ispk = true;
                        }
                        if(ispk)
                        {
                            ispk = false;
                            for (int k = 0; k < pkcount; k++)
                                if (substring2.equals(primarykeys[k]))
                                    ispk = true;
                        }
                    }
                if(!ispk)
                    pks.remove(i);
            }

            for(int i=0;i<pks.size();i++) {
                resArray.add(new ArrayList<>());
                resArray.get(i).add(pks.get(i));
                for (int j = 0; j < facount; j++)
                    if (pks.get(i).equals(fa[0][j]))
                        for(int k=0;k<ac;k++)
                            if(fa[1][j].equals(AttArray[k]) && !aflag[k])
                            {
                                aflag[k]=true;
                                resArray.get(i).add(fa[1][j]);
                            }
            }

            //Print The 2NF Relationship
            System.out.println("Your 2NF Relationship Is:");
            for(int i=0;i<pks.size();i++)
            {
                System.out.print(relname+(i+1)+"(");
                for (int j = 0; j < resArray.get(i).size(); j++) {
                    System.out.print(resArray.get(i).get(j));
                    if (j < resArray.get(i).size() - 1)
                        System.out.print(",");
                }
                System.out.print(")\n");
            }
            NF2=true;
        }

        //Checking For 3NF
        for(int i=0;i<ac;i++)
            aflag[i]=false;
        boolean NF3=true;
        ArrayList<String> unpks=new ArrayList<>();
        for(int i=0;i<facount;i++)
            unpks.add(fa[0][i]);
        for (int i=0;i<unpks.size();i++)
            for(int j=0;j<unpks.size();j++)
                if(unpks.get(i).equals(unpks.get(j))&&i!=j)
                    unpks.remove(j);
        for (int i=unpks.size()-1;i>=0;i--) {
            ispk = false;
            for (int j = 0; j < pkcount; j++)
                if (unpks.get(i).equals(primarykeys[j]))
                    ispk = true;
                else if (unpks.get(i).contains(",")) {
                    ispk = true;
                    int index = 0;
                    String substring1 = unpks.get(i);
                    String substring2 = unpks.get(i);
                    while (substring2.contains(",") && ispk) {
                        ispk = false;
                        index = substring2.indexOf(",", index + 1);
                        substring1 = substring2.substring(0, index);
                        substring2 = substring2.substring(index + 1);
                        for (int k = 0; k < pkcount; k++)
                            if (substring1.equals(primarykeys[k]))
                                ispk = true;
                    }
                    if (ispk) {
                        ispk = false;
                        for (int k = 0; k < pkcount; k++)
                            if (substring2.equals(primarykeys[k]))
                                ispk = true;
                    }
                }
            if (ispk)
                unpks.remove(i);
            else
                NF3=false;
        }
        if(NF3&&NF2)
            System.out.println("Your Relationship Is Already 3NF!");
        else
        {
            System.out.println("Your Relationship Is Not 3NF!");
            for(int i=0;i<unpks.size();i++)
                for(int j=0;j<facount;j++)
                {
                    if(unpks.get(i).equals(fa[0][j]))
                    {
                        for (int k=0;k<resArray.size();k++)
                            for (int l=0;l<resArray.get(k).size();l++)
                            {
                                if(resArray.get(k).get(l).equals(fa[1][j]))
                                    resArray.get(k).remove(l);
                            }
                    }
                }
            for(int i=0;i<unpks.size();i++) {
                resArray.add(new ArrayList<>());
                resArray.get(resArray.size()-1).add(unpks.get(i));
                for (int j = 0; j < facount; j++) {
                    if (unpks.get(i).equals(fa[0][j]))
                        for(int k=0;k<ac;k++)
                            if(fa[1][j].equals(AttArray[k]) && !aflag[k])
                            {
                                aflag[k]=true;
                                resArray.get(resArray.size()-1).add(fa[1][j]);
                            }
                }
            }
            System.out.println("Your 3NF Relationship Is:");
            for(int i=0;i<resArray.size();i++)
            {
                System.out.print(relname+(i+1)+"(");
                for (int j = 0; j < resArray.get(i).size(); j++) {
                    System.out.print(resArray.get(i).get(j));
                    if (j < resArray.get(i).size() - 1)
                        System.out.print(",");
                }
                System.out.print(")\n");
            }
            NF3=true;
        }
    }
}

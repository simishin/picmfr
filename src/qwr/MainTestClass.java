package qwr;

import qwr.model.Base.PublStat;
import qwr.model.SharSystem.RiUser;
import qwr.util.CollectUtl;
import qwr.util.DateTim;

import java.util.ArrayList;

import static java.time.Instant.*;
import static qwr.util.CollectUtl.prnq;

public class MainTestClass {
    public static void main(String[] args) {
        prnq("\n------- Testing ----------");
//        prnq(BgFile.getNowData());
        prnq("int/2=2147483647");
        prnq("int  ="+ Math.toIntExact(now().getEpochSecond()));
        prnq("Время создания int  ="+ DateTim.newSeconds()+
                "\n\t\t"+CollectUtl.prnBinLong(DateTim.newSeconds()));
        //long x=2147483647L; prnq(""+ CollectUtl.prnBolLong(x));
        int user=2147483647;
        user/=8;
        user=16383;
        int xstruc=5;
        int timer=2147483647/8;
        timer/=2;
        long key=2147483647;
        key = (((long)timer & 0x7FFFFFF)<<8)|((long) xstruc & 15)<<52 | ((long) user & 0x3FFF)<<38;

//        key = (xstruc & 0x3F)<<(63-7);
//        key |=(user & 0x3FFFF)<<(63-7-14-4);
//        key |=(timer & 0x3FFFFFF)<<12;
        //key |=(DateTim.newSeconds()& 0x3FFFFFF)<<12;
        prnq("\t\t"+
                ".rrrr.rrrr.ssss.uuuu.uuuu.uuuu.uuUU.tttt&tttt.tttt.tttt.tttt.tttt.tttt.TTTT.TTTT");
        prnq("\t"+ key+ "\n\t\t"+CollectUtl.prnBinLong(key));

        prnq("Time "+timer+"\tUser "+user+"\txStc "+xstruc);

        PublStat.setKeyUser(6);
        RiUser j = new RiUser("log","tit","des", 8);
//        j.incCount();
        RiUser j1 = new RiUser("log","tit","des", 8);
        RiUser j2 = new RiUser("log","tit","des", 8);

//        prnq(" "+j.Count()+"\t "+j.key()+"\t "+j1.key()+"\t "+j2.key());
        prnq("% "+ (PublStat.dupDay()<<2));

        ArrayList<RiUser> riUsers = new ArrayList<>();
        riUsers.add(new RiUser("log","tit","des", 3));
        riUsers.add(new RiUser(704, 3, "log","tit","des", 2));
        riUsers.add(new RiUser(1744, 3, "log","tit","", 4));
        riUsers.add(j);
        riUsers.add(new RiUser(707, 3, "log","tit","dd", 4));
        for (RiUser e: riUsers ) { prnq(""+e.print()); }
        PublStat.setKeyUser(21);
        int h = riUsers.indexOf(j);
        prnq("@ "+h);
        if (h>-1) riUsers.set(h,new RiUser(riUsers.get(h),7));
        for (RiUser e: riUsers ) { prnq(""+e.print()); }
    }//main
    }//class MainTestClass

package com.example.mind_maker.apter;

public class Keyword implements Comparable<Keyword> {
    /**
     * tfidfvalue
     */
    private double tfidfvalue;
    /**
     * name
     */
    private String name;

    public Keyword(String name, double tfidfvalue) {
        this.name = name;
        // tfidf值只保留3位小数
        this.tfidfvalue = (double) Math.round(tfidfvalue * 10000) / 10000;
    }

    public double getTfidfvalue() {
        return tfidfvalue;
    }

    public void setTfidfvalue(double tfidfvalue) {
        this.tfidfvalue = tfidfvalue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 为了在返回tdidf分析结果时，可以按照值的从大到小顺序返回，故实现Comparable接口
     */
    @Override
    public int compareTo(Keyword o) {
        if (this.tfidfvalue - o.tfidfvalue > 0) {
            return -1;
        } else if (this.tfidfvalue - o.tfidfvalue < 0) {
            return 1;
        } else {
            return 0;
        }
        //return this.tfidfvalue-o.tfidfvalue>0?-1:1;
    }

    /**
     * 重写hashcode方法，计算方式与原生String的方法相同
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        long temp;
        temp = Double.doubleToLongBits(tfidfvalue);
        result = PRIME * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Keyword other = (Keyword) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}

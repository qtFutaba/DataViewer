public class StateData
{
    private String name;
    private double murder;
    private double assault;
    private double rape;
    private double urbanPop;

    public StateData()
    {
        this.name = "";
        this.murder = 0;
        this.assault = 0;
        this.rape = 0;
        this.urbanPop = 0;
    }

    public StateData(String name, double murder, double assault, double rape, double urbanPop)
    {
        this.name = name;
        this.murder = murder;
        this.assault = assault;
        this.rape = rape;
        this.urbanPop = urbanPop;
    }

    public void setName(String name){this.name = name;}
    public String getName(){return name;}

    public void setMurder(double murder) {this.murder = murder;}
    public double getMurder() {return murder;}

    public void setAssault(double assault) {this.assault = assault;}
    public double getAssault() {return assault;}

    public void setRape(double rape) {this.rape = rape;}
    public double getRape() {return rape;}

    public void setUrbanPop(double urbanPop) {this.urbanPop = urbanPop;}
    public double getUrbanPop() {return urbanPop;}

    public String toString()
    {
        return name;
    }
}

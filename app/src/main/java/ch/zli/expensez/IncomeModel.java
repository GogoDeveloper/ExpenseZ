package ch.zli.expensez;

public class IncomeModel {
    private float income, fixcosts;

    public IncomeModel() {
    }

    public IncomeModel(float income, float fixcosts) {
        this.income = income;
        this.fixcosts = fixcosts;
    }

    public float getIncome() {
        return income;
    }



    public float getFixcosts() {
        return fixcosts;
    }


}

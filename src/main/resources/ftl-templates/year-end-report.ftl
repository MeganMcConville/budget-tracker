Budget Item	Budgeted Amount	Amount Spent	% Difference	Difference per Month
<#list budgetItems as budgetItem>
${budgetItem.name}	$ ${budgetItem.yearlyBudgetedAmount}	$ ${budgetItem.yearlyAmountSpent}	${budgetItem.percentDifference}%	$ ${budgetItem.averageMonthlyDifference}
</#list>
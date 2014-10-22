package se.kth.id1020.minifs;

public class StringMethods {

	public static boolean matchesWildcard (String input, String criteria)
	{
		//Contains just the wildcard.
		if (criteria.length() == 1)
			return true;
		
		String[] searchStrings = criteria.split("\\*");
		
		for (int i = 0; i < searchStrings.length; i++)
		{
			//If it's the first argument we need to check startsWith, unless first element is a star.
			if (i == 0)
			{
				//If the element is empty 'criteria' started with a * ex. *test, so just skip this element.
				if (searchStrings[i].isEmpty())
					continue;
				else
				{
					if (!input.startsWith(searchStrings[i])) //Criteria is for example stu*nt, check if we start with stu.
						return false;
				}
			}
			else if (i == searchStrings.length-1) //If last element.
			{
				//If our criteria ends with star we don't need to end the 
				if (!criteria.endsWith("*"))
				{
					if(input.endsWith(searchStrings[i])) //Criteria is for example stu*nt, check if we end with nt.
						return true;
					else
						return false;
				}
			}
		
			//Find index for the element in input.
			int index = input.indexOf(searchStrings[i]);	
			
			
			if (index == -1)
				return false;
			
			//We've searched for this, so just remove that part of the string.
			input = input.substring(index + searchStrings[i].length());
		}
		
		return true;
	}
	
}

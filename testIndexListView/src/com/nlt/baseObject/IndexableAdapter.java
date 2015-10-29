package com.nlt.baseObject;

import java.util.ArrayList;
import java.util.zip.Inflater;

import com.nlt.testindexlistview.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class IndexableAdapter extends BaseAdapter implements SectionIndexer
{
//	private final String TAG = "test";
	private ArrayList<String> items;
	private Context context;
	private String[] sections;
	public static String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public IndexableAdapter(Context context, ArrayList<String> items)
	{
		this.context = context;
		this.items = items;
		
		sections = new String[s.length()];
		for (int i = 0; i < s.length(); i++)
		{
			sections[i] = String.valueOf(s.charAt(i));
		}
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		Holder holder;
		if(convertView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.item, null);
			
			holder = new Holder();
			holder.text = (TextView) convertView.findViewById(R.id.item_text);
			
			convertView.setTag(holder);
			
		}
		else
		{
			holder = (Holder) convertView.getTag();
		}
		
		holder.text.setText(items.get(position));
		
		return convertView;
	}
	
	private class Holder
	{
		TextView text;
	}
	

	@Override
	public Object[] getSections()
	{
		// TODO Auto-generated method stub
		return sections;
	}

	@Override
	public int getPositionForSection(int sectionIndex)
	{
		// TODO Auto-generated method stub
		String section = sections[sectionIndex];
		for (int i = 0; i < items.size(); i++)
		{
			String item = items.get(i);
			if (section.length() > item.length())
			{
				continue;
			}
			else if (section.length() == item.length())
			{				
				if (section.equals(item))
				{
					return i;
				}
			}
			else
			{
				char s;
				int index = 0;
				for(int j = 0; j < section.length(); j++)
				{
					s = item.charAt(j);
					if(s == section.charAt(j))
					{
						index++;
						if(index == section.length())
						{
							return i;
						}
					}
					else
					{
						break;
					}
				}		
			}
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position)
	{
		// TODO Auto-generated method stub
		String item = items.get(position);
		for(int i = 0; i < sections.length; i++)
		{
			if(sections[i].charAt(0) == item.charAt(0))
			{
				return i;
			}
		}
		return 0;
	}

}

package com.zxj.shoppingcart.bean;


public class BaseInfo
{
	protected String Id;
	protected String name;
	protected boolean isChoosed;

	public BaseInfo()
	{
		super();
	}

	public BaseInfo(String id, String name)
	{
		super();
		Id = id;
		this.name = name;

	}

	public String getId()
	{
		return Id;
	}

	public void setId(String id)
	{
		Id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isChoosed()
	{
		return isChoosed;
	}

	public void setChoosed(boolean isChoosed)
	{
		this.isChoosed = isChoosed;
	}

}

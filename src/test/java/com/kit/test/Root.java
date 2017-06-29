package com.kit.test;

import com.di.kit.data.annotation.Alias;

/**
 * @author di create by toolkit
 */
@Alias("root")
public class Root {
	private Animals animals;

	public Animals getAnimals() {
		return animals;
	}

	public void setAnimals(Animals animals) {
		this.animals = animals;
	}

	@Alias("animals")
	public static class Animals {
		private Cat cat;
		private java.util.List<Dog> dog;

		public Cat getCat() {
			return cat;
		}

		public void setCat(Cat cat) {
			this.cat = cat;
		}

		public java.util.List<Dog> getDog() {
			return dog;
		}

		public void setDog(java.util.List<Dog> dog) {
			this.dog = dog;
		}

		@Alias("cat")
		public static class Cat {
			private String name;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}
		}

		@Alias("dog")
		public static class Dog {
			private String name;
			private String count;
			private String twoFeet;
			private String breed;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getCount() {
				return count;
			}

			public void setCount(String count) {
				this.count = count;
			}

			public String getTwoFeet() {
				return twoFeet;
			}

			public void setTwoFeet(String twoFeet) {
				this.twoFeet = twoFeet;
			}

			public String getBreed() {
				return breed;
			}

			public void setBreed(String breed) {
				this.breed = breed;
			}
		}
	}
}

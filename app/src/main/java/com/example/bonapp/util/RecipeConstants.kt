package com.example.bonapp.util

object RecipeConstants {
    val CATEGORIES = mapOf(
        "Meal Type" to listOf(
            "Breakfast", "Brunch", "Lunch", "Dinner", "Snack", "Appetizer",
            "Dessert", "Midnight Snack", "Tea Time", "Cocktail Hour"
        ),
        "Cuisine" to listOf(
            "Italian", "Mexican", "Chinese", "Indian", "French", "Japanese",
            "Korean", "Thai", "Vietnamese", "Greek", "Lebanese", "Moroccan",
            "Spanish", "German", "Russian", "American", "Cajun", "Caribbean",
            "Brazilian", "Peruvian", "Ethiopian", "Turkish", "Indonesian"
        ),
        "Dish Type" to listOf(
            "Appetizer", "Main Course", "Side Dish", "Dessert", "Salad", "Soup",
            "Stew", "Casserole", "Pasta", "Pizza", "Burger", "Sandwich", "Wrap",
            "Taco", "Stir-Fry", "Curry", "Smoothie", "Juice", "Cocktail", "Punch"
        ),
        "Dietary" to listOf(
            "Vegetarian", "Vegan", "Gluten-Free", "Low-Carb", "Keto", "Paleo",
            "Whole30", "Raw", "Macrobiotic", "Pescatarian", "Flexitarian",
            "Dairy-Free", "Egg-Free", "Nut-Free", "Soy-Free", "Sugar-Free",
            "Low-Fat", "Low-Sodium", "High-Protein", "Low-Calorie", "Diabetic-Friendly"
        ),
        "Difficulty" to listOf(
            "Easy", "Intermediate", "Advanced", "Expert", "Beginner-Friendly",
            "Quick & Easy", "Kid-Friendly", "Family-Friendly", "Party-Worthy",
            "Date Night", "Weeknight Dinner", "Weekend Project", "Holiday Feast"
        )
    )

    val DIET_TYPES = listOf(
        "Regular", "Vegetarian", "Vegan", "Pescatarian", "Gluten-Free",
        "Dairy-Free", "Nut-Free", "Low-Carb", "Keto", "Paleo",
        "Mediterranean", "Whole30", "Low-Fat", "Low-Sodium", "Diabetic", "FODMAP",
        "DASH", "Atkins", "South Beach", "Zone", "Engine 2", "Ornish",
        "Pritikin", "Macrobiotic", "Raw Food", "Fruitarian", "Volumetrics",
        "Flexitarian", "Intermittent Fasting", "MIND", "Nordic", "Okinawan"
    )

    val TOOLS = listOf(
        "Oven", "Stovetop", "Microwave", "Blender", "Food Processor",
        "Mixer", "Grill", "Slow Cooker", "Pressure Cooker", "Air Fryer",
        "Knife", "Cutting Board", "Measuring Cups", "Measuring Spoons",
        "Baking Sheet", "Saucepan", "Frying Pan", "Wok", "Dutch Oven",
        "Cast Iron Skillet", "Roasting Pan", "Muffin Tin", "Loaf Pan",
        "Springform Pan", "Pie Dish", "Baking Dish", "Casserole Dish",
        "Colander", "Sieve", "Cheese Grater", "Zester", "Peeler",
        "Can Opener", "Corkscrew", "Kitchen Shears", "Tongs", "Spatula",
        "Ladle", "Slotted Spoon", "Whisk", "Rolling Pin", "Pastry Brush",
        "Meat Thermometer", "Instant-Read Thermometer", "Kitchen Scale",
        "Salad Spinner", "Mandoline", "Immersion Blender", "Juicer", "Ice Cream Maker"
    )

    val UNITS = mapOf(
        "Volume" to listOf(
            "teaspoon", "tablespoon", "cup", "fluid ounce", "pint",
            "quart", "gallon", "milliliter", "liter", "drop",
            "coffee spoon", "gill"
        ),
        "Weight" to listOf(
            "ounce", "pound", "gram", "kilogram", "stick", "knob",
            "cube", "block", "tub", "carton", "box", "bag", "sachet", "pod"
        ),
        "Countable" to listOf(
            "clove", "piece", "slice", "wedge", "can", "package",
            "jar", "bottle", "head", "stalk", "bunch", "leaf",
            "ear", "sheet", "strip", "handful", "nub", "nugget",
            "slab", "fillet", "chop", "lobe", "rack", "bulb", "root",
            "cob", "kernel", "seed", "pit", "hull", "scoop", "ball",
            "round", "patty"
        ),
        "Imprecise" to listOf(
            "pinch", "dash", "smidgen", "scruple", "splash", "dollop",
            "to taste", "as needed", "sprig", "peel", "rind", "zest",
            "twist", "grating", "dusting", "drizzle", "squirt", "spray",
            "coating", "layer"
        )
    )
}
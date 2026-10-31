package com.vi5hnu.curvykids.data.content

/**
 * A single illustrated item for the generic "tap-to-learn + find-it" picture topics
 * (Food, Clothes, Jobs, Sports, …). Each renders its hand-drawn [svg] badge.
 */
data class PictureItem(val name: String, val svg: AssetPath)

// ── Food & Treats ───────────────────────────────────────────────────────────────
val FOODS = listOf(
    PictureItem("Baguette", AssetPath("art/food/food-baguette.svg")),
    PictureItem("Bread", AssetPath("art/food/food-bread.svg")),
    PictureItem("Burger", AssetPath("art/food/food-burger.svg")),
    PictureItem("Cake", AssetPath("art/food/food-cake.svg")),
    PictureItem("Candy", AssetPath("art/food/food-candy.svg")),
    PictureItem("Cheese", AssetPath("art/food/food-cheese.svg")),
    PictureItem("Chocolate", AssetPath("art/food/food-chocolate.svg")),
    PictureItem("Cookie", AssetPath("art/food/food-cookie.svg")),
    PictureItem("Croissant", AssetPath("art/food/food-croissant.svg")),
    PictureItem("Cupcake", AssetPath("art/food/food-cupcake.svg")),
    PictureItem("Donut", AssetPath("art/food/food-donut.svg")),
    PictureItem("Egg", AssetPath("art/food/food-egg.svg")),
    PictureItem("Fried Egg", AssetPath("art/food/food-fried-egg.svg")),
    PictureItem("Fries", AssetPath("art/food/food-fries.svg")),
    PictureItem("Hot Dog", AssetPath("art/food/food-hot-dog.svg")),
    PictureItem("Ice Cream", AssetPath("art/food/food-ice-cream.svg")),
    PictureItem("Juice", AssetPath("art/food/food-juice.svg")),
    PictureItem("Lollipop", AssetPath("art/food/food-lollipop.svg")),
    PictureItem("Milk", AssetPath("art/food/food-milk.svg")),
    PictureItem("Noodles", AssetPath("art/food/food-noodles.svg")),
    PictureItem("Pancakes", AssetPath("art/food/food-pancakes.svg")),
    PictureItem("Pasta", AssetPath("art/food/food-pasta.svg")),
    PictureItem("Pizza", AssetPath("art/food/food-pizza.svg")),
    PictureItem("Popcorn", AssetPath("art/food/food-popcorn.svg")),
    PictureItem("Sandwich", AssetPath("art/food/food-sandwich.svg")),
    PictureItem("Taco", AssetPath("art/food/food-taco.svg")),
    PictureItem("Waffle", AssetPath("art/food/food-waffle.svg")),
)

// ── Clothes ─────────────────────────────────────────────────────────────────────
val CLOTHES = listOf(
    PictureItem("Backpack", AssetPath("art/clothes/clothes-backpack.svg")),
    PictureItem("Blouse", AssetPath("art/clothes/clothes-blouse.svg")),
    PictureItem("Boot", AssetPath("art/clothes/clothes-boot.svg")),
    PictureItem("Cap", AssetPath("art/clothes/clothes-cap.svg")),
    PictureItem("Coat", AssetPath("art/clothes/clothes-coat.svg")),
    PictureItem("Crown", AssetPath("art/clothes/clothes-crown.svg")),
    PictureItem("Dress", AssetPath("art/clothes/clothes-dress.svg")),
    PictureItem("Glasses", AssetPath("art/clothes/clothes-glasses.svg")),
    PictureItem("Gloves", AssetPath("art/clothes/clothes-gloves.svg")),
    PictureItem("Handbag", AssetPath("art/clothes/clothes-handbag.svg")),
    PictureItem("Heels", AssetPath("art/clothes/clothes-heels.svg")),
    PictureItem("Hiking Boot", AssetPath("art/clothes/clothes-hiking-boot.svg")),
    PictureItem("Jeans", AssetPath("art/clothes/clothes-jeans.svg")),
    PictureItem("Necktie", AssetPath("art/clothes/clothes-necktie.svg")),
    PictureItem("Sandal", AssetPath("art/clothes/clothes-sandal.svg")),
    PictureItem("Scarf", AssetPath("art/clothes/clothes-scarf.svg")),
    PictureItem("Shoe", AssetPath("art/clothes/clothes-shoe.svg")),
    PictureItem("Sneaker", AssetPath("art/clothes/clothes-sneaker.svg")),
    PictureItem("Socks", AssetPath("art/clothes/clothes-socks.svg")),
    PictureItem("Sun Hat", AssetPath("art/clothes/clothes-sun-hat.svg")),
    PictureItem("T-Shirt", AssetPath("art/clothes/clothes-t-shirt.svg")),
    PictureItem("Top Hat", AssetPath("art/clothes/clothes-top-hat.svg")),
)

// ── Helpers & Jobs ──────────────────────────────────────────────────────────────
val JOBS = listOf(
    PictureItem("Artist", AssetPath("art/jobs/job-artist.svg")),
    PictureItem("Astronaut", AssetPath("art/jobs/job-astronaut.svg")),
    PictureItem("Builder", AssetPath("art/jobs/job-builder.svg")),
    PictureItem("Chef", AssetPath("art/jobs/job-chef.svg")),
    PictureItem("Detective", AssetPath("art/jobs/job-detective.svg")),
    PictureItem("Doctor", AssetPath("art/jobs/job-doctor.svg")),
    PictureItem("Farmer", AssetPath("art/jobs/job-farmer.svg")),
    PictureItem("Firefighter", AssetPath("art/jobs/job-firefighter.svg")),
    PictureItem("Guard", AssetPath("art/jobs/job-guard.svg")),
    PictureItem("Mechanic", AssetPath("art/jobs/job-mechanic.svg")),
    PictureItem("Pilot", AssetPath("art/jobs/job-pilot.svg")),
    PictureItem("Police Officer", AssetPath("art/jobs/job-police-officer.svg")),
    PictureItem("Programmer", AssetPath("art/jobs/job-programmer.svg")),
    PictureItem("Scientist", AssetPath("art/jobs/job-scientist.svg")),
    PictureItem("Teacher", AssetPath("art/jobs/job-teacher.svg")),
)

// ── Sports & Play ───────────────────────────────────────────────────────────────
val SPORTS = listOf(
    PictureItem("Badminton", AssetPath("art/sports/sport-badminton.svg")),
    PictureItem("Baseball", AssetPath("art/sports/sport-baseball.svg")),
    PictureItem("Basketball", AssetPath("art/sports/sport-basketball.svg")),
    PictureItem("Billiards", AssetPath("art/sports/sport-billiards.svg")),
    PictureItem("Boxing", AssetPath("art/sports/sport-boxing.svg")),
    PictureItem("Cricket", AssetPath("art/sports/sport-cricket.svg")),
    PictureItem("Cycling", AssetPath("art/sports/sport-cycling.svg")),
    PictureItem("Football", AssetPath("art/sports/sport-football.svg")),
    PictureItem("Frisbee", AssetPath("art/sports/sport-frisbee.svg")),
    PictureItem("Golf", AssetPath("art/sports/sport-golf.svg")),
    PictureItem("Gymnastics", AssetPath("art/sports/sport-gymnastics.svg")),
    PictureItem("Hockey", AssetPath("art/sports/sport-hockey.svg")),
    PictureItem("Ice Skating", AssetPath("art/sports/sport-ice-skating.svg")),
    PictureItem("Rugby", AssetPath("art/sports/sport-rugby.svg")),
    PictureItem("Skateboard", AssetPath("art/sports/sport-skateboard.svg")),
    PictureItem("Skiing", AssetPath("art/sports/sport-skiing.svg")),
    PictureItem("Snowboard", AssetPath("art/sports/sport-snowboard.svg")),
    PictureItem("Soccer", AssetPath("art/sports/sport-soccer.svg")),
    PictureItem("Swimming", AssetPath("art/sports/sport-swimming.svg")),
    PictureItem("Table Tennis", AssetPath("art/sports/sport-table-tennis.svg")),
    PictureItem("Tennis", AssetPath("art/sports/sport-tennis.svg")),
    PictureItem("Volleyball", AssetPath("art/sports/sport-volleyball.svg")),
)

// ── Instruments ─────────────────────────────────────────────────────────────────
val INSTRUMENTS = listOf(
    PictureItem("Accordion", AssetPath("art/instruments/instrument-accordion.svg")),
    PictureItem("Banjo", AssetPath("art/instruments/instrument-banjo.svg")),
    PictureItem("Bell", AssetPath("art/instruments/instrument-bell.svg")),
    PictureItem("Drum", AssetPath("art/instruments/instrument-drum.svg")),
    PictureItem("Flute", AssetPath("art/instruments/instrument-flute.svg")),
    PictureItem("Guitar", AssetPath("art/instruments/instrument-guitar.svg")),
    PictureItem("Maracas", AssetPath("art/instruments/instrument-maracas.svg")),
    PictureItem("Microphone", AssetPath("art/instruments/instrument-microphone.svg")),
    PictureItem("Piano", AssetPath("art/instruments/instrument-piano.svg")),
    PictureItem("Saxophone", AssetPath("art/instruments/instrument-saxophone.svg")),
    PictureItem("Trumpet", AssetPath("art/instruments/instrument-trumpet.svg")),
    PictureItem("Violin", AssetPath("art/instruments/instrument-violin.svg")),
)

// ── Weather ─────────────────────────────────────────────────────────────────────
val WEATHER_ITEMS = listOf(
    PictureItem("Cloud", AssetPath("art/weather/weather-cloud.svg")),
    PictureItem("Fire", AssetPath("art/weather/weather-fire.svg")),
    PictureItem("Fog", AssetPath("art/weather/weather-fog.svg")),
    PictureItem("Glowing Star", AssetPath("art/weather/weather-glowing-star.svg")),
    PictureItem("Lightning Bolt", AssetPath("art/weather/weather-lightning-bolt.svg")),
    PictureItem("Lightning", AssetPath("art/weather/weather-lightning.svg")),
    PictureItem("Moon", AssetPath("art/weather/weather-moon.svg")),
    PictureItem("Partly Cloudy", AssetPath("art/weather/weather-partly-cloudy.svg")),
    PictureItem("Rain", AssetPath("art/weather/weather-rain.svg")),
    PictureItem("Rainbow", AssetPath("art/weather/weather-rainbow.svg")),
    PictureItem("Snow Cloud", AssetPath("art/weather/weather-snow-cloud.svg")),
    PictureItem("Snowflake", AssetPath("art/weather/weather-snowflake.svg")),
    PictureItem("Snowman", AssetPath("art/weather/weather-snowman.svg")),
    PictureItem("Star", AssetPath("art/weather/weather-star.svg")),
    PictureItem("Sun And Rain", AssetPath("art/weather/weather-sun-and-rain.svg")),
    PictureItem("Sun Behind Cloud", AssetPath("art/weather/weather-sun-behind-cloud.svg")),
    PictureItem("Sun", AssetPath("art/weather/weather-sun.svg")),
    PictureItem("Thermometer", AssetPath("art/weather/weather-thermometer.svg")),
    PictureItem("Thunderstorm", AssetPath("art/weather/weather-thunderstorm.svg")),
    PictureItem("Tornado", AssetPath("art/weather/weather-tornado.svg")),
    PictureItem("Umbrella", AssetPath("art/weather/weather-umbrella.svg")),
    PictureItem("Water Drop", AssetPath("art/weather/weather-water-drop.svg")),
    PictureItem("Wave", AssetPath("art/weather/weather-wave.svg")),
    PictureItem("Wind", AssetPath("art/weather/weather-wind.svg")),
    PictureItem("Windy", AssetPath("art/weather/weather-windy.svg")),
)

// ── Nature & Plants ─────────────────────────────────────────────────────────────
val NATURE = listOf(
    PictureItem("Blossom", AssetPath("art/nature/nature-blossom.svg")),
    PictureItem("Bouquet", AssetPath("art/nature/nature-bouquet.svg")),
    PictureItem("Cactus", AssetPath("art/nature/nature-cactus.svg")),
    PictureItem("Clover", AssetPath("art/nature/nature-clover.svg")),
    PictureItem("Daisy", AssetPath("art/nature/nature-daisy.svg")),
    PictureItem("Fallen Leaves", AssetPath("art/nature/nature-fallen-leaves.svg")),
    PictureItem("Herb", AssetPath("art/nature/nature-herb.svg")),
    PictureItem("Hibiscus", AssetPath("art/nature/nature-hibiscus.svg")),
    PictureItem("Lotus", AssetPath("art/nature/nature-lotus.svg")),
    PictureItem("Maple Leaf", AssetPath("art/nature/nature-maple-leaf.svg")),
    PictureItem("Palm Tree", AssetPath("art/nature/nature-palm-tree.svg")),
    PictureItem("Pine Tree", AssetPath("art/nature/nature-pine-tree.svg")),
    PictureItem("Rock", AssetPath("art/nature/nature-rock.svg")),
    PictureItem("Rose", AssetPath("art/nature/nature-rose.svg")),
    PictureItem("Seedling", AssetPath("art/nature/nature-seedling.svg")),
    PictureItem("Shamrock", AssetPath("art/nature/nature-shamrock.svg")),
    PictureItem("Sunflower", AssetPath("art/nature/nature-sunflower.svg")),
    PictureItem("Tree", AssetPath("art/nature/nature-tree.svg")),
    PictureItem("Tulip", AssetPath("art/nature/nature-tulip.svg")),
    PictureItem("Wheat", AssetPath("art/nature/nature-wheat.svg")),
)

// ── Places ──────────────────────────────────────────────────────────────────────
val PLACES = listOf(
    PictureItem("Bank", AssetPath("art/places/place-bank.svg")),
    PictureItem("Beach", AssetPath("art/places/place-beach.svg")),
    PictureItem("Bridge", AssetPath("art/places/place-bridge.svg")),
    PictureItem("Castle", AssetPath("art/places/place-castle.svg")),
    PictureItem("Church", AssetPath("art/places/place-church.svg")),
    PictureItem("Factory", AssetPath("art/places/place-factory.svg")),
    PictureItem("Fountain", AssetPath("art/places/place-fountain.svg")),
    PictureItem("Home Garden", AssetPath("art/places/place-home-garden.svg")),
    PictureItem("Hospital", AssetPath("art/places/place-hospital.svg")),
    PictureItem("House", AssetPath("art/places/place-house.svg")),
    PictureItem("Mall", AssetPath("art/places/place-mall.svg")),
    PictureItem("Mosque", AssetPath("art/places/place-mosque.svg")),
    PictureItem("Museum", AssetPath("art/places/place-museum.svg")),
    PictureItem("Palace", AssetPath("art/places/place-palace.svg")),
    PictureItem("School", AssetPath("art/places/place-school.svg")),
    PictureItem("Stadium", AssetPath("art/places/place-stadium.svg")),
    PictureItem("Station", AssetPath("art/places/place-station.svg")),
    PictureItem("Store", AssetPath("art/places/place-store.svg")),
    PictureItem("Tent", AssetPath("art/places/place-tent.svg")),
    PictureItem("Tower", AssetPath("art/places/place-tower.svg")),
)

// ── Around the Home ─────────────────────────────────────────────────────────────
val HOME_ITEMS = listOf(
    PictureItem("Basket", AssetPath("art/around-home/home-basket.svg")),
    PictureItem("Bathtub", AssetPath("art/around-home/home-bathtub.svg")),
    PictureItem("Bed", AssetPath("art/around-home/home-bed.svg")),
    PictureItem("Broom", AssetPath("art/around-home/home-broom.svg")),
    PictureItem("Bucket", AssetPath("art/around-home/home-bucket.svg")),
    PictureItem("Cart", AssetPath("art/around-home/home-cart.svg")),
    PictureItem("Chair", AssetPath("art/around-home/home-chair.svg")),
    PictureItem("Clock", AssetPath("art/around-home/home-clock.svg")),
    PictureItem("Door", AssetPath("art/around-home/home-door.svg")),
    PictureItem("Key", AssetPath("art/around-home/home-key.svg")),
    PictureItem("Ladder", AssetPath("art/around-home/home-ladder.svg")),
    PictureItem("Light Bulb", AssetPath("art/around-home/home-light-bulb.svg")),
    PictureItem("Mirror", AssetPath("art/around-home/home-mirror.svg")),
    PictureItem("Plug", AssetPath("art/around-home/home-plug.svg")),
    PictureItem("Shower", AssetPath("art/around-home/home-shower.svg")),
    PictureItem("Soap Bottle", AssetPath("art/around-home/home-soap-bottle.svg")),
    PictureItem("Sofa", AssetPath("art/around-home/home-sofa.svg")),
    PictureItem("Telephone", AssetPath("art/around-home/home-telephone.svg")),
    PictureItem("Television", AssetPath("art/around-home/home-television.svg")),
    PictureItem("Toilet", AssetPath("art/around-home/home-toilet.svg")),
    PictureItem("Toothbrush", AssetPath("art/around-home/home-toothbrush.svg")),
    PictureItem("Window", AssetPath("art/around-home/home-window.svg")),
)

// ── Birds ───────────────────────────────────────────────────────────────────────
val BIRDS = listOf(
    PictureItem("Baby Chick", AssetPath("art/birds/bird-baby-chick.svg")),
    PictureItem("Bird", AssetPath("art/birds/bird-bird.svg")),
    PictureItem("Blackbird", AssetPath("art/birds/bird-blackbird.svg")),
    PictureItem("Chick", AssetPath("art/birds/bird-chick.svg")),
    PictureItem("Dove", AssetPath("art/birds/bird-dove.svg")),
    PictureItem("Duck", AssetPath("art/birds/bird-duck.svg")),
    PictureItem("Eagle", AssetPath("art/birds/bird-eagle.svg")),
    PictureItem("Flamingo", AssetPath("art/birds/bird-flamingo.svg")),
    PictureItem("Goose", AssetPath("art/birds/bird-goose.svg")),
    PictureItem("Hatching Chick", AssetPath("art/birds/bird-hatching-chick.svg")),
    PictureItem("Hen", AssetPath("art/birds/bird-hen.svg")),
    PictureItem("Owl", AssetPath("art/birds/bird-owl.svg")),
    PictureItem("Parrot", AssetPath("art/birds/bird-parrot.svg")),
    PictureItem("Peacock", AssetPath("art/birds/bird-peacock.svg")),
    PictureItem("Penguin", AssetPath("art/birds/bird-penguin.svg")),
    PictureItem("Rooster", AssetPath("art/birds/bird-rooster.svg")),
    PictureItem("Swan", AssetPath("art/birds/bird-swan.svg")),
    PictureItem("Turkey", AssetPath("art/birds/bird-turkey.svg")),
)

// ── Sea Animals ─────────────────────────────────────────────────────────────────
val SEA_ANIMALS = listOf(
    PictureItem("Crab", AssetPath("art/sea-animals/sea-crab.svg")),
    PictureItem("Crocodile", AssetPath("art/sea-animals/sea-crocodile.svg")),
    PictureItem("Dolphin", AssetPath("art/sea-animals/sea-dolphin.svg")),
    PictureItem("Fish", AssetPath("art/sea-animals/sea-fish.svg")),
    PictureItem("Jellyfish", AssetPath("art/sea-animals/sea-jellyfish.svg")),
    PictureItem("Lobster", AssetPath("art/sea-animals/sea-lobster.svg")),
    PictureItem("Octopus", AssetPath("art/sea-animals/sea-octopus.svg")),
    PictureItem("Pufferfish", AssetPath("art/sea-animals/sea-pufferfish.svg")),
    PictureItem("Sea Turtle", AssetPath("art/sea-animals/sea-sea-turtle.svg")),
    PictureItem("Seal", AssetPath("art/sea-animals/sea-seal.svg")),
    PictureItem("Seashell", AssetPath("art/sea-animals/sea-seashell.svg")),
    PictureItem("Shark", AssetPath("art/sea-animals/sea-shark.svg")),
    PictureItem("Shrimp", AssetPath("art/sea-animals/sea-shrimp.svg")),
    PictureItem("Squid", AssetPath("art/sea-animals/sea-squid.svg")),
    PictureItem("Tropical Fish", AssetPath("art/sea-animals/sea-tropical-fish.svg")),
    PictureItem("Whale", AssetPath("art/sea-animals/sea-whale.svg")),
)

// ── Insects & Bugs ──────────────────────────────────────────────────────────────
val INSECTS = listOf(
    PictureItem("Ant", AssetPath("art/insects/insect-ant.svg")),
    PictureItem("Bee", AssetPath("art/insects/insect-bee.svg")),
    PictureItem("Beetle", AssetPath("art/insects/insect-beetle.svg")),
    PictureItem("Butterfly", AssetPath("art/insects/insect-butterfly.svg")),
    PictureItem("Caterpillar", AssetPath("art/insects/insect-caterpillar.svg")),
    PictureItem("Cockroach", AssetPath("art/insects/insect-cockroach.svg")),
    PictureItem("Cricket", AssetPath("art/insects/insect-cricket.svg")),
    PictureItem("Fly", AssetPath("art/insects/insect-fly.svg")),
    PictureItem("Ladybug", AssetPath("art/insects/insect-ladybug.svg")),
    PictureItem("Mosquito", AssetPath("art/insects/insect-mosquito.svg")),
    PictureItem("Scorpion", AssetPath("art/insects/insect-scorpion.svg")),
    PictureItem("Snail", AssetPath("art/insects/insect-snail.svg")),
    PictureItem("Spider", AssetPath("art/insects/insect-spider.svg")),
    PictureItem("Worm", AssetPath("art/insects/insect-worm.svg")),
)

// ── Toys & Party ────────────────────────────────────────────────────────────────
val TOYS = listOf(
    PictureItem("Balloon", AssetPath("art/toys/toy-balloon.svg")),
    PictureItem("Bow", AssetPath("art/toys/toy-bow.svg")),
    PictureItem("Carousel", AssetPath("art/toys/toy-carousel.svg")),
    PictureItem("Confetti", AssetPath("art/toys/toy-confetti.svg")),
    PictureItem("Dartboard", AssetPath("art/toys/toy-dartboard.svg")),
    PictureItem("Dice", AssetPath("art/toys/toy-dice.svg")),
    PictureItem("Ferris Wheel", AssetPath("art/toys/toy-ferris-wheel.svg")),
    PictureItem("Game Controller", AssetPath("art/toys/toy-game-controller.svg")),
    PictureItem("Gift", AssetPath("art/toys/toy-gift.svg")),
    PictureItem("Kite", AssetPath("art/toys/toy-kite.svg")),
    PictureItem("Nesting Dolls", AssetPath("art/toys/toy-nesting-dolls.svg")),
    PictureItem("Party Popper", AssetPath("art/toys/toy-party-popper.svg")),
    PictureItem("Piñata", AssetPath("art/toys/toy-pi-ata.svg")),
    PictureItem("Puzzle", AssetPath("art/toys/toy-puzzle.svg")),
    PictureItem("Roller Coaster", AssetPath("art/toys/toy-roller-coaster.svg")),
    PictureItem("Slide", AssetPath("art/toys/toy-slide.svg")),
    PictureItem("Streamers", AssetPath("art/toys/toy-streamers.svg")),
    PictureItem("Teddy Bear", AssetPath("art/toys/toy-teddy-bear.svg")),
    PictureItem("Yo-Yo", AssetPath("art/toys/toy-yo-yo.svg")),
)

/**
 * Describes a generic picture topic: its item pool, the small uppercase quiz header, and the
 * celebration title. Keyed by [Topic.id] in [PICTURE_SPECS] so one screen + one nav case serve
 * every picture topic (open/closed: add a topic by adding data, not code).
 */
data class PictureSpec(
    val items: List<PictureItem>,
    val promptLabel: String,
    val celebrateTitle: String,
)

val PICTURE_SPECS: Map<String, PictureSpec> = mapOf(
    "birds"       to PictureSpec(BIRDS, "FIND THE BIRD", "Bird Star!"),
    "sea"         to PictureSpec(SEA_ANIMALS, "FIND THE SEA ANIMAL", "Splash!"),
    "insects"     to PictureSpec(INSECTS, "FIND THE BUG", "Bug Star!"),
    "food"        to PictureSpec(FOODS, "FIND THE FOOD", "Yummy!"),
    "clothes"     to PictureSpec(CLOTHES, "FIND THE CLOTHING", "Dressed Up!"),
    "toys"        to PictureSpec(TOYS, "FIND THE TOY", "Playtime!"),
    "instruments" to PictureSpec(INSTRUMENTS, "FIND THE INSTRUMENT", "Musical!"),
    "home"        to PictureSpec(HOME_ITEMS, "FIND IT AT HOME", "Home Star!"),
    "jobs"        to PictureSpec(JOBS, "WHO DOES THIS JOB?", "Helper Star!"),
    "places"      to PictureSpec(PLACES, "FIND THE PLACE", "Explorer!"),
    "sports"      to PictureSpec(SPORTS, "FIND THE SPORT", "Sporty!"),
    "weather"     to PictureSpec(WEATHER_ITEMS, "FIND THE WEATHER", "Weather Star!"),
    "nature"      to PictureSpec(NATURE, "FIND IT IN NATURE", "Nature Star!"),
)

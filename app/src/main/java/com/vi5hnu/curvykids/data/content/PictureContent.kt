package com.vi5hnu.curvykids.data.content

/**
 * A single illustrated item for the generic "tap-to-learn + find-it" picture topics
 * (Food, Clothes, Jobs, Sports, …). Each renders its hand-drawn [svg] badge.
 */
data class PictureItem(val name: String, val svg: AssetPath)

// ── Food & Treats ───────────────────────────────────────────────────────────────
val FOODS = listOf(
    PictureItem("Baguette", AssetPath("kids/food/food-baguette.svg")),
    PictureItem("Bread", AssetPath("kids/food/food-bread.svg")),
    PictureItem("Burger", AssetPath("kids/food/food-burger.svg")),
    PictureItem("Cake", AssetPath("kids/food/food-cake.svg")),
    PictureItem("Candy", AssetPath("kids/food/food-candy.svg")),
    PictureItem("Cheese", AssetPath("kids/food/food-cheese.svg")),
    PictureItem("Chocolate", AssetPath("kids/food/food-chocolate.svg")),
    PictureItem("Cookie", AssetPath("kids/food/food-cookie.svg")),
    PictureItem("Croissant", AssetPath("kids/food/food-croissant.svg")),
    PictureItem("Cupcake", AssetPath("kids/food/food-cupcake.svg")),
    PictureItem("Donut", AssetPath("kids/food/food-donut.svg")),
    PictureItem("Egg", AssetPath("kids/food/food-egg.svg")),
    PictureItem("Fried Egg", AssetPath("kids/food/food-fried-egg.svg")),
    PictureItem("Fries", AssetPath("kids/food/food-fries.svg")),
    PictureItem("Hot Dog", AssetPath("kids/food/food-hot-dog.svg")),
    PictureItem("Ice Cream", AssetPath("kids/food/food-ice-cream.svg")),
    PictureItem("Juice", AssetPath("kids/food/food-juice.svg")),
    PictureItem("Lollipop", AssetPath("kids/food/food-lollipop.svg")),
    PictureItem("Milk", AssetPath("kids/food/food-milk.svg")),
    PictureItem("Noodles", AssetPath("kids/food/food-noodles.svg")),
    PictureItem("Pancakes", AssetPath("kids/food/food-pancakes.svg")),
    PictureItem("Pasta", AssetPath("kids/food/food-pasta.svg")),
    PictureItem("Pizza", AssetPath("kids/food/food-pizza.svg")),
    PictureItem("Popcorn", AssetPath("kids/food/food-popcorn.svg")),
    PictureItem("Sandwich", AssetPath("kids/food/food-sandwich.svg")),
    PictureItem("Taco", AssetPath("kids/food/food-taco.svg")),
    PictureItem("Waffle", AssetPath("kids/food/food-waffle.svg")),
)

// ── Clothes ─────────────────────────────────────────────────────────────────────
val CLOTHES = listOf(
    PictureItem("Backpack", AssetPath("kids/clothes/clothes-backpack.svg")),
    PictureItem("Blouse", AssetPath("kids/clothes/clothes-blouse.svg")),
    PictureItem("Boot", AssetPath("kids/clothes/clothes-boot.svg")),
    PictureItem("Cap", AssetPath("kids/clothes/clothes-cap.svg")),
    PictureItem("Coat", AssetPath("kids/clothes/clothes-coat.svg")),
    PictureItem("Crown", AssetPath("kids/clothes/clothes-crown.svg")),
    PictureItem("Dress", AssetPath("kids/clothes/clothes-dress.svg")),
    PictureItem("Glasses", AssetPath("kids/clothes/clothes-glasses.svg")),
    PictureItem("Gloves", AssetPath("kids/clothes/clothes-gloves.svg")),
    PictureItem("Handbag", AssetPath("kids/clothes/clothes-handbag.svg")),
    PictureItem("Heels", AssetPath("kids/clothes/clothes-heels.svg")),
    PictureItem("Hiking Boot", AssetPath("kids/clothes/clothes-hiking-boot.svg")),
    PictureItem("Jeans", AssetPath("kids/clothes/clothes-jeans.svg")),
    PictureItem("Necktie", AssetPath("kids/clothes/clothes-necktie.svg")),
    PictureItem("Sandal", AssetPath("kids/clothes/clothes-sandal.svg")),
    PictureItem("Scarf", AssetPath("kids/clothes/clothes-scarf.svg")),
    PictureItem("Shoe", AssetPath("kids/clothes/clothes-shoe.svg")),
    PictureItem("Sneaker", AssetPath("kids/clothes/clothes-sneaker.svg")),
    PictureItem("Socks", AssetPath("kids/clothes/clothes-socks.svg")),
    PictureItem("Sun Hat", AssetPath("kids/clothes/clothes-sun-hat.svg")),
    PictureItem("T-Shirt", AssetPath("kids/clothes/clothes-t-shirt.svg")),
    PictureItem("Top Hat", AssetPath("kids/clothes/clothes-top-hat.svg")),
)

// ── Helpers & Jobs ──────────────────────────────────────────────────────────────
val JOBS = listOf(
    PictureItem("Artist", AssetPath("kids/jobs/job-artist.svg")),
    PictureItem("Astronaut", AssetPath("kids/jobs/job-astronaut.svg")),
    PictureItem("Builder", AssetPath("kids/jobs/job-builder.svg")),
    PictureItem("Chef", AssetPath("kids/jobs/job-chef.svg")),
    PictureItem("Detective", AssetPath("kids/jobs/job-detective.svg")),
    PictureItem("Doctor", AssetPath("kids/jobs/job-doctor.svg")),
    PictureItem("Farmer", AssetPath("kids/jobs/job-farmer.svg")),
    PictureItem("Firefighter", AssetPath("kids/jobs/job-firefighter.svg")),
    PictureItem("Guard", AssetPath("kids/jobs/job-guard.svg")),
    PictureItem("Mechanic", AssetPath("kids/jobs/job-mechanic.svg")),
    PictureItem("Pilot", AssetPath("kids/jobs/job-pilot.svg")),
    PictureItem("Police Officer", AssetPath("kids/jobs/job-police-officer.svg")),
    PictureItem("Programmer", AssetPath("kids/jobs/job-programmer.svg")),
    PictureItem("Scientist", AssetPath("kids/jobs/job-scientist.svg")),
    PictureItem("Teacher", AssetPath("kids/jobs/job-teacher.svg")),
)

// ── Sports & Play ───────────────────────────────────────────────────────────────
val SPORTS = listOf(
    PictureItem("Badminton", AssetPath("kids/sports/sport-badminton.svg")),
    PictureItem("Baseball", AssetPath("kids/sports/sport-baseball.svg")),
    PictureItem("Basketball", AssetPath("kids/sports/sport-basketball.svg")),
    PictureItem("Billiards", AssetPath("kids/sports/sport-billiards.svg")),
    PictureItem("Boxing", AssetPath("kids/sports/sport-boxing.svg")),
    PictureItem("Cricket", AssetPath("kids/sports/sport-cricket.svg")),
    PictureItem("Cycling", AssetPath("kids/sports/sport-cycling.svg")),
    PictureItem("Football", AssetPath("kids/sports/sport-football.svg")),
    PictureItem("Frisbee", AssetPath("kids/sports/sport-frisbee.svg")),
    PictureItem("Golf", AssetPath("kids/sports/sport-golf.svg")),
    PictureItem("Gymnastics", AssetPath("kids/sports/sport-gymnastics.svg")),
    PictureItem("Hockey", AssetPath("kids/sports/sport-hockey.svg")),
    PictureItem("Ice Skating", AssetPath("kids/sports/sport-ice-skating.svg")),
    PictureItem("Rugby", AssetPath("kids/sports/sport-rugby.svg")),
    PictureItem("Skateboard", AssetPath("kids/sports/sport-skateboard.svg")),
    PictureItem("Skiing", AssetPath("kids/sports/sport-skiing.svg")),
    PictureItem("Snowboard", AssetPath("kids/sports/sport-snowboard.svg")),
    PictureItem("Soccer", AssetPath("kids/sports/sport-soccer.svg")),
    PictureItem("Swimming", AssetPath("kids/sports/sport-swimming.svg")),
    PictureItem("Table Tennis", AssetPath("kids/sports/sport-table-tennis.svg")),
    PictureItem("Tennis", AssetPath("kids/sports/sport-tennis.svg")),
    PictureItem("Volleyball", AssetPath("kids/sports/sport-volleyball.svg")),
)

// ── Instruments ─────────────────────────────────────────────────────────────────
val INSTRUMENTS = listOf(
    PictureItem("Accordion", AssetPath("kids/instruments/instrument-accordion.svg")),
    PictureItem("Banjo", AssetPath("kids/instruments/instrument-banjo.svg")),
    PictureItem("Bell", AssetPath("kids/instruments/instrument-bell.svg")),
    PictureItem("Drum", AssetPath("kids/instruments/instrument-drum.svg")),
    PictureItem("Flute", AssetPath("kids/instruments/instrument-flute.svg")),
    PictureItem("Guitar", AssetPath("kids/instruments/instrument-guitar.svg")),
    PictureItem("Maracas", AssetPath("kids/instruments/instrument-maracas.svg")),
    PictureItem("Microphone", AssetPath("kids/instruments/instrument-microphone.svg")),
    PictureItem("Piano", AssetPath("kids/instruments/instrument-piano.svg")),
    PictureItem("Saxophone", AssetPath("kids/instruments/instrument-saxophone.svg")),
    PictureItem("Trumpet", AssetPath("kids/instruments/instrument-trumpet.svg")),
    PictureItem("Violin", AssetPath("kids/instruments/instrument-violin.svg")),
)

// ── Weather ─────────────────────────────────────────────────────────────────────
val WEATHER_ITEMS = listOf(
    PictureItem("Cloud", AssetPath("kids/weather/weather-cloud.svg")),
    PictureItem("Fire", AssetPath("kids/weather/weather-fire.svg")),
    PictureItem("Fog", AssetPath("kids/weather/weather-fog.svg")),
    PictureItem("Glowing Star", AssetPath("kids/weather/weather-glowing-star.svg")),
    PictureItem("Lightning Bolt", AssetPath("kids/weather/weather-lightning-bolt.svg")),
    PictureItem("Lightning", AssetPath("kids/weather/weather-lightning.svg")),
    PictureItem("Moon", AssetPath("kids/weather/weather-moon.svg")),
    PictureItem("Partly Cloudy", AssetPath("kids/weather/weather-partly-cloudy.svg")),
    PictureItem("Rain", AssetPath("kids/weather/weather-rain.svg")),
    PictureItem("Rainbow", AssetPath("kids/weather/weather-rainbow.svg")),
    PictureItem("Snow Cloud", AssetPath("kids/weather/weather-snow-cloud.svg")),
    PictureItem("Snowflake", AssetPath("kids/weather/weather-snowflake.svg")),
    PictureItem("Snowman", AssetPath("kids/weather/weather-snowman.svg")),
    PictureItem("Star", AssetPath("kids/weather/weather-star.svg")),
    PictureItem("Sun And Rain", AssetPath("kids/weather/weather-sun-and-rain.svg")),
    PictureItem("Sun Behind Cloud", AssetPath("kids/weather/weather-sun-behind-cloud.svg")),
    PictureItem("Sun", AssetPath("kids/weather/weather-sun.svg")),
    PictureItem("Thermometer", AssetPath("kids/weather/weather-thermometer.svg")),
    PictureItem("Thunderstorm", AssetPath("kids/weather/weather-thunderstorm.svg")),
    PictureItem("Tornado", AssetPath("kids/weather/weather-tornado.svg")),
    PictureItem("Umbrella", AssetPath("kids/weather/weather-umbrella.svg")),
    PictureItem("Water Drop", AssetPath("kids/weather/weather-water-drop.svg")),
    PictureItem("Wave", AssetPath("kids/weather/weather-wave.svg")),
    PictureItem("Wind", AssetPath("kids/weather/weather-wind.svg")),
    PictureItem("Windy", AssetPath("kids/weather/weather-windy.svg")),
)

// ── Nature & Plants ─────────────────────────────────────────────────────────────
val NATURE = listOf(
    PictureItem("Blossom", AssetPath("kids/nature/nature-blossom.svg")),
    PictureItem("Bouquet", AssetPath("kids/nature/nature-bouquet.svg")),
    PictureItem("Cactus", AssetPath("kids/nature/nature-cactus.svg")),
    PictureItem("Clover", AssetPath("kids/nature/nature-clover.svg")),
    PictureItem("Daisy", AssetPath("kids/nature/nature-daisy.svg")),
    PictureItem("Fallen Leaves", AssetPath("kids/nature/nature-fallen-leaves.svg")),
    PictureItem("Herb", AssetPath("kids/nature/nature-herb.svg")),
    PictureItem("Hibiscus", AssetPath("kids/nature/nature-hibiscus.svg")),
    PictureItem("Lotus", AssetPath("kids/nature/nature-lotus.svg")),
    PictureItem("Maple Leaf", AssetPath("kids/nature/nature-maple-leaf.svg")),
    PictureItem("Palm Tree", AssetPath("kids/nature/nature-palm-tree.svg")),
    PictureItem("Pine Tree", AssetPath("kids/nature/nature-pine-tree.svg")),
    PictureItem("Rock", AssetPath("kids/nature/nature-rock.svg")),
    PictureItem("Rose", AssetPath("kids/nature/nature-rose.svg")),
    PictureItem("Seedling", AssetPath("kids/nature/nature-seedling.svg")),
    PictureItem("Shamrock", AssetPath("kids/nature/nature-shamrock.svg")),
    PictureItem("Sunflower", AssetPath("kids/nature/nature-sunflower.svg")),
    PictureItem("Tree", AssetPath("kids/nature/nature-tree.svg")),
    PictureItem("Tulip", AssetPath("kids/nature/nature-tulip.svg")),
    PictureItem("Wheat", AssetPath("kids/nature/nature-wheat.svg")),
)

// ── Places ──────────────────────────────────────────────────────────────────────
val PLACES = listOf(
    PictureItem("Bank", AssetPath("kids/places/place-bank.svg")),
    PictureItem("Beach", AssetPath("kids/places/place-beach.svg")),
    PictureItem("Bridge", AssetPath("kids/places/place-bridge.svg")),
    PictureItem("Castle", AssetPath("kids/places/place-castle.svg")),
    PictureItem("Church", AssetPath("kids/places/place-church.svg")),
    PictureItem("Factory", AssetPath("kids/places/place-factory.svg")),
    PictureItem("Fountain", AssetPath("kids/places/place-fountain.svg")),
    PictureItem("Home Garden", AssetPath("kids/places/place-home-garden.svg")),
    PictureItem("Hospital", AssetPath("kids/places/place-hospital.svg")),
    PictureItem("House", AssetPath("kids/places/place-house.svg")),
    PictureItem("Mall", AssetPath("kids/places/place-mall.svg")),
    PictureItem("Mosque", AssetPath("kids/places/place-mosque.svg")),
    PictureItem("Museum", AssetPath("kids/places/place-museum.svg")),
    PictureItem("Palace", AssetPath("kids/places/place-palace.svg")),
    PictureItem("School", AssetPath("kids/places/place-school.svg")),
    PictureItem("Stadium", AssetPath("kids/places/place-stadium.svg")),
    PictureItem("Station", AssetPath("kids/places/place-station.svg")),
    PictureItem("Store", AssetPath("kids/places/place-store.svg")),
    PictureItem("Tent", AssetPath("kids/places/place-tent.svg")),
    PictureItem("Tower", AssetPath("kids/places/place-tower.svg")),
)

// ── Around the Home ─────────────────────────────────────────────────────────────
val HOME_ITEMS = listOf(
    PictureItem("Basket", AssetPath("kids/around-home/home-basket.svg")),
    PictureItem("Bathtub", AssetPath("kids/around-home/home-bathtub.svg")),
    PictureItem("Bed", AssetPath("kids/around-home/home-bed.svg")),
    PictureItem("Broom", AssetPath("kids/around-home/home-broom.svg")),
    PictureItem("Bucket", AssetPath("kids/around-home/home-bucket.svg")),
    PictureItem("Cart", AssetPath("kids/around-home/home-cart.svg")),
    PictureItem("Chair", AssetPath("kids/around-home/home-chair.svg")),
    PictureItem("Clock", AssetPath("kids/around-home/home-clock.svg")),
    PictureItem("Door", AssetPath("kids/around-home/home-door.svg")),
    PictureItem("Key", AssetPath("kids/around-home/home-key.svg")),
    PictureItem("Ladder", AssetPath("kids/around-home/home-ladder.svg")),
    PictureItem("Light Bulb", AssetPath("kids/around-home/home-light-bulb.svg")),
    PictureItem("Mirror", AssetPath("kids/around-home/home-mirror.svg")),
    PictureItem("Plug", AssetPath("kids/around-home/home-plug.svg")),
    PictureItem("Shower", AssetPath("kids/around-home/home-shower.svg")),
    PictureItem("Soap Bottle", AssetPath("kids/around-home/home-soap-bottle.svg")),
    PictureItem("Sofa", AssetPath("kids/around-home/home-sofa.svg")),
    PictureItem("Telephone", AssetPath("kids/around-home/home-telephone.svg")),
    PictureItem("Television", AssetPath("kids/around-home/home-television.svg")),
    PictureItem("Toilet", AssetPath("kids/around-home/home-toilet.svg")),
    PictureItem("Toothbrush", AssetPath("kids/around-home/home-toothbrush.svg")),
    PictureItem("Window", AssetPath("kids/around-home/home-window.svg")),
)

// ── Birds ───────────────────────────────────────────────────────────────────────
val BIRDS = listOf(
    PictureItem("Baby Chick", AssetPath("kids/birds/bird-baby-chick.svg")),
    PictureItem("Bird", AssetPath("kids/birds/bird-bird.svg")),
    PictureItem("Blackbird", AssetPath("kids/birds/bird-blackbird.svg")),
    PictureItem("Chick", AssetPath("kids/birds/bird-chick.svg")),
    PictureItem("Dove", AssetPath("kids/birds/bird-dove.svg")),
    PictureItem("Duck", AssetPath("kids/birds/bird-duck.svg")),
    PictureItem("Eagle", AssetPath("kids/birds/bird-eagle.svg")),
    PictureItem("Flamingo", AssetPath("kids/birds/bird-flamingo.svg")),
    PictureItem("Goose", AssetPath("kids/birds/bird-goose.svg")),
    PictureItem("Hatching Chick", AssetPath("kids/birds/bird-hatching-chick.svg")),
    PictureItem("Hen", AssetPath("kids/birds/bird-hen.svg")),
    PictureItem("Owl", AssetPath("kids/birds/bird-owl.svg")),
    PictureItem("Parrot", AssetPath("kids/birds/bird-parrot.svg")),
    PictureItem("Peacock", AssetPath("kids/birds/bird-peacock.svg")),
    PictureItem("Penguin", AssetPath("kids/birds/bird-penguin.svg")),
    PictureItem("Rooster", AssetPath("kids/birds/bird-rooster.svg")),
    PictureItem("Swan", AssetPath("kids/birds/bird-swan.svg")),
    PictureItem("Turkey", AssetPath("kids/birds/bird-turkey.svg")),
)

// ── Sea Animals ─────────────────────────────────────────────────────────────────
val SEA_ANIMALS = listOf(
    PictureItem("Crab", AssetPath("kids/sea-animals/sea-crab.svg")),
    PictureItem("Crocodile", AssetPath("kids/sea-animals/sea-crocodile.svg")),
    PictureItem("Dolphin", AssetPath("kids/sea-animals/sea-dolphin.svg")),
    PictureItem("Fish", AssetPath("kids/sea-animals/sea-fish.svg")),
    PictureItem("Jellyfish", AssetPath("kids/sea-animals/sea-jellyfish.svg")),
    PictureItem("Lobster", AssetPath("kids/sea-animals/sea-lobster.svg")),
    PictureItem("Octopus", AssetPath("kids/sea-animals/sea-octopus.svg")),
    PictureItem("Pufferfish", AssetPath("kids/sea-animals/sea-pufferfish.svg")),
    PictureItem("Sea Turtle", AssetPath("kids/sea-animals/sea-sea-turtle.svg")),
    PictureItem("Seal", AssetPath("kids/sea-animals/sea-seal.svg")),
    PictureItem("Seashell", AssetPath("kids/sea-animals/sea-seashell.svg")),
    PictureItem("Shark", AssetPath("kids/sea-animals/sea-shark.svg")),
    PictureItem("Shrimp", AssetPath("kids/sea-animals/sea-shrimp.svg")),
    PictureItem("Squid", AssetPath("kids/sea-animals/sea-squid.svg")),
    PictureItem("Tropical Fish", AssetPath("kids/sea-animals/sea-tropical-fish.svg")),
    PictureItem("Whale", AssetPath("kids/sea-animals/sea-whale.svg")),
)

// ── Insects & Bugs ──────────────────────────────────────────────────────────────
val INSECTS = listOf(
    PictureItem("Ant", AssetPath("kids/insects/insect-ant.svg")),
    PictureItem("Bee", AssetPath("kids/insects/insect-bee.svg")),
    PictureItem("Beetle", AssetPath("kids/insects/insect-beetle.svg")),
    PictureItem("Butterfly", AssetPath("kids/insects/insect-butterfly.svg")),
    PictureItem("Caterpillar", AssetPath("kids/insects/insect-caterpillar.svg")),
    PictureItem("Cockroach", AssetPath("kids/insects/insect-cockroach.svg")),
    PictureItem("Cricket", AssetPath("kids/insects/insect-cricket.svg")),
    PictureItem("Fly", AssetPath("kids/insects/insect-fly.svg")),
    PictureItem("Ladybug", AssetPath("kids/insects/insect-ladybug.svg")),
    PictureItem("Mosquito", AssetPath("kids/insects/insect-mosquito.svg")),
    PictureItem("Scorpion", AssetPath("kids/insects/insect-scorpion.svg")),
    PictureItem("Snail", AssetPath("kids/insects/insect-snail.svg")),
    PictureItem("Spider", AssetPath("kids/insects/insect-spider.svg")),
    PictureItem("Worm", AssetPath("kids/insects/insect-worm.svg")),
)

// ── Toys & Party ────────────────────────────────────────────────────────────────
val TOYS = listOf(
    PictureItem("Balloon", AssetPath("kids/toys/toy-balloon.svg")),
    PictureItem("Bow", AssetPath("kids/toys/toy-bow.svg")),
    PictureItem("Carousel", AssetPath("kids/toys/toy-carousel.svg")),
    PictureItem("Confetti", AssetPath("kids/toys/toy-confetti.svg")),
    PictureItem("Dartboard", AssetPath("kids/toys/toy-dartboard.svg")),
    PictureItem("Dice", AssetPath("kids/toys/toy-dice.svg")),
    PictureItem("Ferris Wheel", AssetPath("kids/toys/toy-ferris-wheel.svg")),
    PictureItem("Game Controller", AssetPath("kids/toys/toy-game-controller.svg")),
    PictureItem("Gift", AssetPath("kids/toys/toy-gift.svg")),
    PictureItem("Kite", AssetPath("kids/toys/toy-kite.svg")),
    PictureItem("Nesting Dolls", AssetPath("kids/toys/toy-nesting-dolls.svg")),
    PictureItem("Party Popper", AssetPath("kids/toys/toy-party-popper.svg")),
    PictureItem("Piñata", AssetPath("kids/toys/toy-pi-ata.svg")),
    PictureItem("Puzzle", AssetPath("kids/toys/toy-puzzle.svg")),
    PictureItem("Roller Coaster", AssetPath("kids/toys/toy-roller-coaster.svg")),
    PictureItem("Slide", AssetPath("kids/toys/toy-slide.svg")),
    PictureItem("Streamers", AssetPath("kids/toys/toy-streamers.svg")),
    PictureItem("Teddy Bear", AssetPath("kids/toys/toy-teddy-bear.svg")),
    PictureItem("Yo-Yo", AssetPath("kids/toys/toy-yo-yo.svg")),
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

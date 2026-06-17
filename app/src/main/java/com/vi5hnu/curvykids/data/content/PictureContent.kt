package com.vi5hnu.curvykids.data.content

/**
 * A single illustrated item for the generic "tap-to-learn + find-it" picture topics
 * (Food, Clothes, Jobs, Sports, …). Each renders its [svg] badge (with [emoji] as fallback).
 */
data class PictureItem(val name: String, val emoji: String, val svg: String)

// ── Food & Treats ───────────────────────────────────────────────────────────────
val FOODS = listOf(
    PictureItem("Baguette", "🥖", "kids/food/food-baguette.svg"),
    PictureItem("Bread", "🍞", "kids/food/food-bread.svg"),
    PictureItem("Burger", "🍔", "kids/food/food-burger.svg"),
    PictureItem("Cake", "🎂", "kids/food/food-cake.svg"),
    PictureItem("Candy", "🍬", "kids/food/food-candy.svg"),
    PictureItem("Cheese", "🧀", "kids/food/food-cheese.svg"),
    PictureItem("Chocolate", "🍫", "kids/food/food-chocolate.svg"),
    PictureItem("Cookie", "🍪", "kids/food/food-cookie.svg"),
    PictureItem("Croissant", "🥐", "kids/food/food-croissant.svg"),
    PictureItem("Cupcake", "🧁", "kids/food/food-cupcake.svg"),
    PictureItem("Donut", "🍩", "kids/food/food-donut.svg"),
    PictureItem("Egg", "🥚", "kids/food/food-egg.svg"),
    PictureItem("Fried Egg", "🍳", "kids/food/food-fried-egg.svg"),
    PictureItem("Fries", "🍟", "kids/food/food-fries.svg"),
    PictureItem("Hot Dog", "🌭", "kids/food/food-hot-dog.svg"),
    PictureItem("Ice Cream", "🍦", "kids/food/food-ice-cream.svg"),
    PictureItem("Juice", "🧃", "kids/food/food-juice.svg"),
    PictureItem("Lollipop", "🍭", "kids/food/food-lollipop.svg"),
    PictureItem("Milk", "🥛", "kids/food/food-milk.svg"),
    PictureItem("Noodles", "🍜", "kids/food/food-noodles.svg"),
    PictureItem("Pancakes", "🥞", "kids/food/food-pancakes.svg"),
    PictureItem("Pasta", "🍝", "kids/food/food-pasta.svg"),
    PictureItem("Pizza", "🍕", "kids/food/food-pizza.svg"),
    PictureItem("Popcorn", "🍿", "kids/food/food-popcorn.svg"),
    PictureItem("Sandwich", "🥪", "kids/food/food-sandwich.svg"),
    PictureItem("Taco", "🌮", "kids/food/food-taco.svg"),
    PictureItem("Waffle", "🧇", "kids/food/food-waffle.svg"),
)

// ── Clothes ─────────────────────────────────────────────────────────────────────
val CLOTHES = listOf(
    PictureItem("Backpack", "🎒", "kids/clothes/clothes-backpack.svg"),
    PictureItem("Blouse", "👚", "kids/clothes/clothes-blouse.svg"),
    PictureItem("Boot", "👢", "kids/clothes/clothes-boot.svg"),
    PictureItem("Cap", "🧢", "kids/clothes/clothes-cap.svg"),
    PictureItem("Coat", "🧥", "kids/clothes/clothes-coat.svg"),
    PictureItem("Crown", "👑", "kids/clothes/clothes-crown.svg"),
    PictureItem("Dress", "👗", "kids/clothes/clothes-dress.svg"),
    PictureItem("Glasses", "👓", "kids/clothes/clothes-glasses.svg"),
    PictureItem("Gloves", "🧤", "kids/clothes/clothes-gloves.svg"),
    PictureItem("Handbag", "👜", "kids/clothes/clothes-handbag.svg"),
    PictureItem("Heels", "👠", "kids/clothes/clothes-heels.svg"),
    PictureItem("Hiking Boot", "🥾", "kids/clothes/clothes-hiking-boot.svg"),
    PictureItem("Jeans", "👖", "kids/clothes/clothes-jeans.svg"),
    PictureItem("Necktie", "👔", "kids/clothes/clothes-necktie.svg"),
    PictureItem("Sandal", "🩴", "kids/clothes/clothes-sandal.svg"),
    PictureItem("Scarf", "🧣", "kids/clothes/clothes-scarf.svg"),
    PictureItem("Shoe", "👞", "kids/clothes/clothes-shoe.svg"),
    PictureItem("Sneaker", "👟", "kids/clothes/clothes-sneaker.svg"),
    PictureItem("Socks", "🧦", "kids/clothes/clothes-socks.svg"),
    PictureItem("Sun Hat", "👒", "kids/clothes/clothes-sun-hat.svg"),
    PictureItem("T-Shirt", "👕", "kids/clothes/clothes-t-shirt.svg"),
    PictureItem("Top Hat", "🎩", "kids/clothes/clothes-top-hat.svg"),
)

// ── Helpers & Jobs ──────────────────────────────────────────────────────────────
val JOBS = listOf(
    PictureItem("Artist", "🧑‍🎨", "kids/jobs/job-artist.svg"),
    PictureItem("Astronaut", "🧑‍🚀", "kids/jobs/job-astronaut.svg"),
    PictureItem("Builder", "👷", "kids/jobs/job-builder.svg"),
    PictureItem("Chef", "🧑‍🍳", "kids/jobs/job-chef.svg"),
    PictureItem("Detective", "🕵️", "kids/jobs/job-detective.svg"),
    PictureItem("Doctor", "🧑‍⚕️", "kids/jobs/job-doctor.svg"),
    PictureItem("Farmer", "🧑‍🌾", "kids/jobs/job-farmer.svg"),
    PictureItem("Firefighter", "🧑‍🚒", "kids/jobs/job-firefighter.svg"),
    PictureItem("Guard", "💂", "kids/jobs/job-guard.svg"),
    PictureItem("Mechanic", "🧑‍🔧", "kids/jobs/job-mechanic.svg"),
    PictureItem("Pilot", "🧑‍✈️", "kids/jobs/job-pilot.svg"),
    PictureItem("Police Officer", "👮", "kids/jobs/job-police-officer.svg"),
    PictureItem("Programmer", "🧑‍💻", "kids/jobs/job-programmer.svg"),
    PictureItem("Scientist", "🧑‍🔬", "kids/jobs/job-scientist.svg"),
    PictureItem("Teacher", "🧑‍🏫", "kids/jobs/job-teacher.svg"),
)

// ── Sports & Play ───────────────────────────────────────────────────────────────
val SPORTS = listOf(
    PictureItem("Badminton", "🏸", "kids/sports/sport-badminton.svg"),
    PictureItem("Baseball", "⚾", "kids/sports/sport-baseball.svg"),
    PictureItem("Basketball", "🏀", "kids/sports/sport-basketball.svg"),
    PictureItem("Billiards", "🎱", "kids/sports/sport-billiards.svg"),
    PictureItem("Boxing", "🥊", "kids/sports/sport-boxing.svg"),
    PictureItem("Cricket", "🏏", "kids/sports/sport-cricket.svg"),
    PictureItem("Cycling", "🚴", "kids/sports/sport-cycling.svg"),
    PictureItem("Football", "🏈", "kids/sports/sport-football.svg"),
    PictureItem("Frisbee", "🥏", "kids/sports/sport-frisbee.svg"),
    PictureItem("Golf", "⛳", "kids/sports/sport-golf.svg"),
    PictureItem("Gymnastics", "🤸", "kids/sports/sport-gymnastics.svg"),
    PictureItem("Hockey", "🏒", "kids/sports/sport-hockey.svg"),
    PictureItem("Ice Skating", "⛸️", "kids/sports/sport-ice-skating.svg"),
    PictureItem("Rugby", "🏉", "kids/sports/sport-rugby.svg"),
    PictureItem("Skateboard", "🛹", "kids/sports/sport-skateboard.svg"),
    PictureItem("Skiing", "🎿", "kids/sports/sport-skiing.svg"),
    PictureItem("Snowboard", "🏂", "kids/sports/sport-snowboard.svg"),
    PictureItem("Soccer", "⚽", "kids/sports/sport-soccer.svg"),
    PictureItem("Swimming", "🏊", "kids/sports/sport-swimming.svg"),
    PictureItem("Table Tennis", "🏓", "kids/sports/sport-table-tennis.svg"),
    PictureItem("Tennis", "🎾", "kids/sports/sport-tennis.svg"),
    PictureItem("Volleyball", "🏐", "kids/sports/sport-volleyball.svg"),
)

// ── Instruments ─────────────────────────────────────────────────────────────────
val INSTRUMENTS = listOf(
    PictureItem("Accordion", "🪗", "kids/instruments/instrument-accordion.svg"),
    PictureItem("Banjo", "🪕", "kids/instruments/instrument-banjo.svg"),
    PictureItem("Bell", "🔔", "kids/instruments/instrument-bell.svg"),
    PictureItem("Drum", "🥁", "kids/instruments/instrument-drum.svg"),
    PictureItem("Flute", "🪈", "kids/instruments/instrument-flute.svg"),
    PictureItem("Guitar", "🎸", "kids/instruments/instrument-guitar.svg"),
    PictureItem("Maracas", "🪇", "kids/instruments/instrument-maracas.svg"),
    PictureItem("Microphone", "🎤", "kids/instruments/instrument-microphone.svg"),
    PictureItem("Piano", "🎹", "kids/instruments/instrument-piano.svg"),
    PictureItem("Saxophone", "🎷", "kids/instruments/instrument-saxophone.svg"),
    PictureItem("Trumpet", "🎺", "kids/instruments/instrument-trumpet.svg"),
    PictureItem("Violin", "🎻", "kids/instruments/instrument-violin.svg"),
)

// ── Weather ─────────────────────────────────────────────────────────────────────
val WEATHER_ITEMS = listOf(
    PictureItem("Cloud", "☁️", "kids/weather/weather-cloud.svg"),
    PictureItem("Fire", "🔥", "kids/weather/weather-fire.svg"),
    PictureItem("Fog", "🌫️", "kids/weather/weather-fog.svg"),
    PictureItem("Glowing Star", "🌟", "kids/weather/weather-glowing-star.svg"),
    PictureItem("Lightning Bolt", "⚡", "kids/weather/weather-lightning-bolt.svg"),
    PictureItem("Lightning", "🌩️", "kids/weather/weather-lightning.svg"),
    PictureItem("Moon", "🌙", "kids/weather/weather-moon.svg"),
    PictureItem("Partly Cloudy", "⛅", "kids/weather/weather-partly-cloudy.svg"),
    PictureItem("Rain", "🌧️", "kids/weather/weather-rain.svg"),
    PictureItem("Rainbow", "🌈", "kids/weather/weather-rainbow.svg"),
    PictureItem("Snow Cloud", "🌨️", "kids/weather/weather-snow-cloud.svg"),
    PictureItem("Snowflake", "❄️", "kids/weather/weather-snowflake.svg"),
    PictureItem("Snowman", "☃️", "kids/weather/weather-snowman.svg"),
    PictureItem("Star", "⭐", "kids/weather/weather-star.svg"),
    PictureItem("Sun And Rain", "🌦️", "kids/weather/weather-sun-and-rain.svg"),
    PictureItem("Sun Behind Cloud", "🌤️", "kids/weather/weather-sun-behind-cloud.svg"),
    PictureItem("Sun", "☀️", "kids/weather/weather-sun.svg"),
    PictureItem("Thermometer", "🌡️", "kids/weather/weather-thermometer.svg"),
    PictureItem("Thunderstorm", "⛈️", "kids/weather/weather-thunderstorm.svg"),
    PictureItem("Tornado", "🌪️", "kids/weather/weather-tornado.svg"),
    PictureItem("Umbrella", "☔", "kids/weather/weather-umbrella.svg"),
    PictureItem("Water Drop", "💧", "kids/weather/weather-water-drop.svg"),
    PictureItem("Wave", "🌊", "kids/weather/weather-wave.svg"),
    PictureItem("Wind", "💨", "kids/weather/weather-wind.svg"),
    PictureItem("Windy", "🌬️", "kids/weather/weather-windy.svg"),
)

// ── Nature & Plants ─────────────────────────────────────────────────────────────
val NATURE = listOf(
    PictureItem("Blossom", "🌸", "kids/nature/nature-blossom.svg"),
    PictureItem("Bouquet", "💐", "kids/nature/nature-bouquet.svg"),
    PictureItem("Cactus", "🌵", "kids/nature/nature-cactus.svg"),
    PictureItem("Clover", "🍀", "kids/nature/nature-clover.svg"),
    PictureItem("Daisy", "🌼", "kids/nature/nature-daisy.svg"),
    PictureItem("Fallen Leaves", "🍂", "kids/nature/nature-fallen-leaves.svg"),
    PictureItem("Herb", "🌿", "kids/nature/nature-herb.svg"),
    PictureItem("Hibiscus", "🌺", "kids/nature/nature-hibiscus.svg"),
    PictureItem("Lotus", "🪷", "kids/nature/nature-lotus.svg"),
    PictureItem("Maple Leaf", "🍁", "kids/nature/nature-maple-leaf.svg"),
    PictureItem("Palm Tree", "🌴", "kids/nature/nature-palm-tree.svg"),
    PictureItem("Pine Tree", "🌲", "kids/nature/nature-pine-tree.svg"),
    PictureItem("Rock", "🪨", "kids/nature/nature-rock.svg"),
    PictureItem("Rose", "🌹", "kids/nature/nature-rose.svg"),
    PictureItem("Seedling", "🌱", "kids/nature/nature-seedling.svg"),
    PictureItem("Shamrock", "☘️", "kids/nature/nature-shamrock.svg"),
    PictureItem("Sunflower", "🌻", "kids/nature/nature-sunflower.svg"),
    PictureItem("Tree", "🌳", "kids/nature/nature-tree.svg"),
    PictureItem("Tulip", "🌷", "kids/nature/nature-tulip.svg"),
    PictureItem("Wheat", "🌾", "kids/nature/nature-wheat.svg"),
)

// ── Places ──────────────────────────────────────────────────────────────────────
val PLACES = listOf(
    PictureItem("Bank", "🏦", "kids/places/place-bank.svg"),
    PictureItem("Beach", "🏖️", "kids/places/place-beach.svg"),
    PictureItem("Bridge", "🌉", "kids/places/place-bridge.svg"),
    PictureItem("Castle", "🏰", "kids/places/place-castle.svg"),
    PictureItem("Church", "⛪", "kids/places/place-church.svg"),
    PictureItem("Factory", "🏭", "kids/places/place-factory.svg"),
    PictureItem("Fountain", "⛲", "kids/places/place-fountain.svg"),
    PictureItem("Home Garden", "🏡", "kids/places/place-home-garden.svg"),
    PictureItem("Hospital", "🏥", "kids/places/place-hospital.svg"),
    PictureItem("House", "🏠", "kids/places/place-house.svg"),
    PictureItem("Mall", "🏬", "kids/places/place-mall.svg"),
    PictureItem("Mosque", "🕌", "kids/places/place-mosque.svg"),
    PictureItem("Museum", "🏛️", "kids/places/place-museum.svg"),
    PictureItem("Palace", "🏯", "kids/places/place-palace.svg"),
    PictureItem("School", "🏫", "kids/places/place-school.svg"),
    PictureItem("Stadium", "🏟️", "kids/places/place-stadium.svg"),
    PictureItem("Station", "🚉", "kids/places/place-station.svg"),
    PictureItem("Store", "🏪", "kids/places/place-store.svg"),
    PictureItem("Tent", "⛺", "kids/places/place-tent.svg"),
    PictureItem("Tower", "🗼", "kids/places/place-tower.svg"),
)

// ── Around the Home ─────────────────────────────────────────────────────────────
val HOME_ITEMS = listOf(
    PictureItem("Basket", "🧺", "kids/around-home/home-basket.svg"),
    PictureItem("Bathtub", "🛁", "kids/around-home/home-bathtub.svg"),
    PictureItem("Bed", "🛏️", "kids/around-home/home-bed.svg"),
    PictureItem("Broom", "🧹", "kids/around-home/home-broom.svg"),
    PictureItem("Bucket", "🪣", "kids/around-home/home-bucket.svg"),
    PictureItem("Cart", "🛒", "kids/around-home/home-cart.svg"),
    PictureItem("Chair", "🪑", "kids/around-home/home-chair.svg"),
    PictureItem("Clock", "🕰️", "kids/around-home/home-clock.svg"),
    PictureItem("Door", "🚪", "kids/around-home/home-door.svg"),
    PictureItem("Key", "🔑", "kids/around-home/home-key.svg"),
    PictureItem("Ladder", "🪜", "kids/around-home/home-ladder.svg"),
    PictureItem("Light Bulb", "💡", "kids/around-home/home-light-bulb.svg"),
    PictureItem("Mirror", "🪞", "kids/around-home/home-mirror.svg"),
    PictureItem("Plug", "🔌", "kids/around-home/home-plug.svg"),
    PictureItem("Shower", "🚿", "kids/around-home/home-shower.svg"),
    PictureItem("Soap Bottle", "🧴", "kids/around-home/home-soap-bottle.svg"),
    PictureItem("Sofa", "🛋️", "kids/around-home/home-sofa.svg"),
    PictureItem("Telephone", "☎️", "kids/around-home/home-telephone.svg"),
    PictureItem("Television", "📺", "kids/around-home/home-television.svg"),
    PictureItem("Toilet", "🚽", "kids/around-home/home-toilet.svg"),
    PictureItem("Toothbrush", "🪥", "kids/around-home/home-toothbrush.svg"),
    PictureItem("Window", "🪟", "kids/around-home/home-window.svg"),
)

// ── Birds ───────────────────────────────────────────────────────────────────────
val BIRDS = listOf(
    PictureItem("Baby Chick", "🐤", "kids/birds/bird-baby-chick.svg"),
    PictureItem("Bird", "🐦", "kids/birds/bird-bird.svg"),
    PictureItem("Blackbird", "🐦‍⬛", "kids/birds/bird-blackbird.svg"),
    PictureItem("Chick", "🐥", "kids/birds/bird-chick.svg"),
    PictureItem("Dove", "🕊️", "kids/birds/bird-dove.svg"),
    PictureItem("Duck", "🦆", "kids/birds/bird-duck.svg"),
    PictureItem("Eagle", "🦅", "kids/birds/bird-eagle.svg"),
    PictureItem("Flamingo", "🦩", "kids/birds/bird-flamingo.svg"),
    PictureItem("Goose", "🪿", "kids/birds/bird-goose.svg"),
    PictureItem("Hatching Chick", "🐣", "kids/birds/bird-hatching-chick.svg"),
    PictureItem("Hen", "🐔", "kids/birds/bird-hen.svg"),
    PictureItem("Owl", "🦉", "kids/birds/bird-owl.svg"),
    PictureItem("Parrot", "🦜", "kids/birds/bird-parrot.svg"),
    PictureItem("Peacock", "🦚", "kids/birds/bird-peacock.svg"),
    PictureItem("Penguin", "🐧", "kids/birds/bird-penguin.svg"),
    PictureItem("Rooster", "🐓", "kids/birds/bird-rooster.svg"),
    PictureItem("Swan", "🦢", "kids/birds/bird-swan.svg"),
    PictureItem("Turkey", "🦃", "kids/birds/bird-turkey.svg"),
)

// ── Sea Animals ─────────────────────────────────────────────────────────────────
val SEA_ANIMALS = listOf(
    PictureItem("Crab", "🦀", "kids/sea-animals/sea-crab.svg"),
    PictureItem("Crocodile", "🐊", "kids/sea-animals/sea-crocodile.svg"),
    PictureItem("Dolphin", "🐬", "kids/sea-animals/sea-dolphin.svg"),
    PictureItem("Fish", "🐟", "kids/sea-animals/sea-fish.svg"),
    PictureItem("Jellyfish", "🪼", "kids/sea-animals/sea-jellyfish.svg"),
    PictureItem("Lobster", "🦞", "kids/sea-animals/sea-lobster.svg"),
    PictureItem("Octopus", "🐙", "kids/sea-animals/sea-octopus.svg"),
    PictureItem("Pufferfish", "🐡", "kids/sea-animals/sea-pufferfish.svg"),
    PictureItem("Sea Turtle", "🐢", "kids/sea-animals/sea-sea-turtle.svg"),
    PictureItem("Seal", "🦭", "kids/sea-animals/sea-seal.svg"),
    PictureItem("Seashell", "🐚", "kids/sea-animals/sea-seashell.svg"),
    PictureItem("Shark", "🦈", "kids/sea-animals/sea-shark.svg"),
    PictureItem("Shrimp", "🦐", "kids/sea-animals/sea-shrimp.svg"),
    PictureItem("Squid", "🦑", "kids/sea-animals/sea-squid.svg"),
    PictureItem("Tropical Fish", "🐠", "kids/sea-animals/sea-tropical-fish.svg"),
    PictureItem("Whale", "🐳", "kids/sea-animals/sea-whale.svg"),
)

// ── Insects & Bugs ──────────────────────────────────────────────────────────────
val INSECTS = listOf(
    PictureItem("Ant", "🐜", "kids/insects/insect-ant.svg"),
    PictureItem("Bee", "🐝", "kids/insects/insect-bee.svg"),
    PictureItem("Beetle", "🪲", "kids/insects/insect-beetle.svg"),
    PictureItem("Butterfly", "🦋", "kids/insects/insect-butterfly.svg"),
    PictureItem("Caterpillar", "🐛", "kids/insects/insect-caterpillar.svg"),
    PictureItem("Cockroach", "🪳", "kids/insects/insect-cockroach.svg"),
    PictureItem("Cricket", "🦗", "kids/insects/insect-cricket.svg"),
    PictureItem("Fly", "🪰", "kids/insects/insect-fly.svg"),
    PictureItem("Ladybug", "🐞", "kids/insects/insect-ladybug.svg"),
    PictureItem("Mosquito", "🦟", "kids/insects/insect-mosquito.svg"),
    PictureItem("Scorpion", "🦂", "kids/insects/insect-scorpion.svg"),
    PictureItem("Snail", "🐌", "kids/insects/insect-snail.svg"),
    PictureItem("Spider", "🕷️", "kids/insects/insect-spider.svg"),
    PictureItem("Worm", "🪱", "kids/insects/insect-worm.svg"),
)

// ── Toys & Party ────────────────────────────────────────────────────────────────
val TOYS = listOf(
    PictureItem("Balloon", "🎈", "kids/toys/toy-balloon.svg"),
    PictureItem("Bow", "🎀", "kids/toys/toy-bow.svg"),
    PictureItem("Carousel", "🎠", "kids/toys/toy-carousel.svg"),
    PictureItem("Confetti", "🎊", "kids/toys/toy-confetti.svg"),
    PictureItem("Dartboard", "🎯", "kids/toys/toy-dartboard.svg"),
    PictureItem("Dice", "🎲", "kids/toys/toy-dice.svg"),
    PictureItem("Ferris Wheel", "🎡", "kids/toys/toy-ferris-wheel.svg"),
    PictureItem("Game Controller", "🎮", "kids/toys/toy-game-controller.svg"),
    PictureItem("Gift", "🎁", "kids/toys/toy-gift.svg"),
    PictureItem("Kite", "🪁", "kids/toys/toy-kite.svg"),
    PictureItem("Nesting Dolls", "🪆", "kids/toys/toy-nesting-dolls.svg"),
    PictureItem("Party Popper", "🎉", "kids/toys/toy-party-popper.svg"),
    PictureItem("Piñata", "🪅", "kids/toys/toy-pi-ata.svg"),
    PictureItem("Puzzle", "🧩", "kids/toys/toy-puzzle.svg"),
    PictureItem("Roller Coaster", "🎢", "kids/toys/toy-roller-coaster.svg"),
    PictureItem("Slide", "🛝", "kids/toys/toy-slide.svg"),
    PictureItem("Streamers", "🎏", "kids/toys/toy-streamers.svg"),
    PictureItem("Teddy Bear", "🧸", "kids/toys/toy-teddy-bear.svg"),
    PictureItem("Yo-Yo", "🪀", "kids/toys/toy-yo-yo.svg"),
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

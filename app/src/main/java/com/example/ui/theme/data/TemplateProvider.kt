package com.example.data

import java.util.UUID

object TemplateProvider {

    fun generateGamingTemplateFiles(workspaceId: Long): List<ProjectFile> {
        val indexHtml = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FREE FIRE MAX: Portal & Squad Simulator</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="void-glitch-layer"></div>
    <header>
        <div class="logo-container">
            <h1 class="neon-title" data-text="FREE FIRE MAX">FREE FIRE MAX</h1>
            <p class="subtitle">V.O.I.D. Squad Companion & Weapons Guide</p>
        </div>
    </header>

    <main class="dashboard">
        <!-- Character / Squad Hub -->
        <section class="card character-hub">
            <h2 class="section-title">⚡ Squad Character Sync</h2>
            <div class="character-grid">
                <div class="char-card active" onclick="selectChar('Alok')">
                    <div class="char-avatar">🎵</div>
                    <h3>DJ Alok</h3>
                    <p class="skill-name">Drop the Beat</p>
                    <p class="skill-desc">Creates 5m aura; restores 5 HP/sec, boosts movement speed by 15%.</p>
                </div>
                <div class="char-card" onclick="selectChar('Chrono')">
                    <div class="char-avatar">🛡️</div>
                    <h3>Chrono</h3>
                    <p class="skill-name">Time Turner</p>
                    <p class="skill-desc">Creates an impenetrable force field blocking 800 damage.</p>
                </div>
                <div class="char-card" onclick="selectChar('Wukong')">
                    <div class="char-avatar">🍃</div>
                    <h3>Wukong</h3>
                    <p class="skill-name">Camouflage</p>
                    <p class="skill-desc">Transforms into a bush, reducing movement speed by 10% for 15s.</p>
                </div>
            </div>
            
            <div class="squad-simulator">
                <h3>Build Active Squad Boost</h3>
                <div class="squad-slots">
                    <div class="slot" id="slot-1">🎵 Alok (Leader)</div>
                    <div class="slot" id="slot-2">Empty</div>
                    <div class="slot" id="slot-3">Empty</div>
                </div>
                <button class="btn btn-primary" onclick="simulateActiveBoost()">Activate Squad Synergy</button>
                <div class="combat-log" id="combat-log">Squad ready for Free Fire MAX Combat.</div>
            </div>
        </section>

        <!-- Weapon Intel -->
        <section class="card weapon-intel">
            <h2 class="section-title">🔫 Weapons Guide & Loot Wheel</h2>
            <div class="weapons-selector">
                <select id="weapon-select" onchange="updateWeaponGuide()">
                    <option value="mp40">MP40 (Submachine Gun)</option>
                    <option value="m1014">M1014 (Shotgun)</option>
                    <option value="awm">AWM (Sniper Rifle)</option>
                    <option value="woodpecker">Woodpecker (Marksman Rifle)</option>
                </select>
            </div>

            <div class="weapon-details" id="weapon-details">
                <h3 id="w-name">MP40 - Special Ops</h3>
                <div class="stats-bar">
                    <div class="stat"><span class="label">Damage:</span> <div class="bar-bg"><div class="bar" id="w-dmg" style="width: 48%;">48%</div></div></div>
                    <div class="stat"><span class="label">Rate of Fire:</span> <div class="bar-bg"><div class="bar" id="w-rof" style="width: 83%;">83%</div></div></div>
                    <div class="stat"><span class="label">Range:</span> <div class="bar-bg"><div class="bar" id="w-rng" style="width: 22%;">22%</div></div></div>
                </div>
                <p id="w-desc" class="weapon-desc">The ultimate close-range beast in Free Fire. Dominates with insane fire rate!</p>
            </div>

            <!-- Spin Royale -->
            <div class="spin-royale">
                <h3>V.O.I.D. Spin Royale</h3>
                <button class="btn btn-secondary" onclick="spinRoyale()">Spin Weapon Crate</button>
                <div class="spin-result" id="spin-result">Get ready for dynamic rewards...</div>
            </div>
        </section>
    </main>

    <footer>
        <p>&copy; 2026 V.O.I.D. Systems. Jujutsu Kaisen Supremacy programmatically enforced.</p>
    </footer>

    <script src="script.js"></script>
</body>
</html>
""".trimIndent()

        val styleCss = """/* V.O.I.D. Cyber-Red Free Fire Theme */
:root {
    --bg-dark: #05010c;
    --card-bg: #110825;
    --neon-red: #ff0055;
    --neon-gold: #ffaa00;
    --border-color: #3b1764;
    --text-primary: #f1edfa;
    --text-secondary: #9784b8;
}

body {
    margin: 0;
    padding: 0;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background-color: var(--bg-dark);
    color: var(--text-primary);
    overflow-x: hidden;
    line-height: 1.6;
}

header {
    background: linear-gradient(135deg, #1b0033 0%, var(--bg-dark) 100%);
    padding: 30px 15px;
    text-align: center;
    border-bottom: 2px solid var(--neon-red);
    box-shadow: 0 0 20px rgba(255, 0, 85, 0.3);
}

.neon-title {
    font-size: 2.8rem;
    font-weight: 900;
    color: #fff;
    text-shadow: 0 0 5px #fff, 0 0 10px var(--neon-red), 0 0 25px var(--neon-red);
    margin: 0;
    text-transform: uppercase;
    letter-spacing: 3px;
}

.subtitle {
    color: var(--neon-gold);
    font-size: 1.1rem;
    margin: 5px 0 0 0;
    letter-spacing: 1px;
}

.dashboard {
    max-width: 1200px;
    margin: 30px auto;
    padding: 0 15px;
    display: grid;
    grid-template-columns: 1fr;
    gap: 25px;
}

@media(min-width: 768px) {
    .dashboard {
        grid-template-columns: 1fr 1fr;
    }
}

.card {
    background-color: var(--card-bg);
    border: 1px solid var(--border-color);
    border-radius: 12px;
    padding: 25px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
    transition: transform 0.3s ease, border-color 0.3s ease;
}

.card:hover {
    transform: translateY(-5px);
    border-color: var(--neon-red);
}

.section-title {
    font-size: 1.5rem;
    color: var(--text-primary);
    border-bottom: 2px solid var(--border-color);
    padding-bottom: 10px;
    margin-top: 0;
    margin-bottom: 20px;
}

.character-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 15px;
    margin-bottom: 20px;
}

.char-card {
    background-color: #0c041d;
    border: 1px solid var(--border-color);
    border-radius: 8px;
    padding: 15px;
    text-align: center;
    cursor: pointer;
    transition: all 0.2s ease;
}

.char-card.active, .char-card:hover {
    border-color: var(--neon-gold);
    background-color: #1e0b3c;
    box-shadow: 0 0 15px rgba(255, 170, 0, 0.15);
}

.char-avatar {
    font-size: 2rem;
    margin-bottom: 10px;
}

.char-card h3 {
    margin: 5px 0;
    font-size: 1rem;
}

.skill-name {
    color: var(--neon-gold);
    font-size: 0.85rem;
    font-weight: bold;
    margin: 0;
}

.skill-desc {
    font-size: 0.75rem;
    color: var(--text-secondary);
    margin: 5px 0 0 0;
}

.squad-simulator {
    background-color: #06020c;
    border: 1px dashed var(--border-color);
    border-radius: 8px;
    padding: 15px;
    margin-top: 20px;
}

.squad-slots {
    display: flex;
    justify-content: space-around;
    gap: 10px;
    margin-bottom: 15px;
}

.slot {
    flex: 1;
    background-color: #14082c;
    border: 1px solid var(--border-color);
    border-radius: 6px;
    padding: 10px;
    font-size: 0.85rem;
    text-align: center;
}

.btn {
    display: block;
    width: 100%;
    border: none;
    border-radius: 6px;
    padding: 12px;
    font-weight: bold;
    cursor: pointer;
    text-transform: uppercase;
    font-size: 0.95rem;
    transition: all 0.2s ease;
}

.btn-primary {
    background-color: var(--neon-red);
    color: #fff;
}

.btn-primary:hover {
    background-color: #ff3377;
    box-shadow: 0 0 15px var(--neon-red);
}

.btn-secondary {
    background-color: var(--neon-gold);
    color: #000;
}

.btn-secondary:hover {
    background-color: #ffbb33;
    box-shadow: 0 0 15px var(--neon-gold);
}

.combat-log {
    margin-top: 15px;
    font-family: 'Courier New', Courier, monospace;
    font-size: 0.8rem;
    background-color: #000;
    color: var(--neon-gold);
    padding: 10px;
    border-radius: 4px;
    min-height: 40px;
    border-left: 3px solid var(--neon-red);
}

.weapons-selector select {
    width: 100%;
    padding: 10px;
    background-color: #0c041d;
    color: var(--text-primary);
    border: 1px solid var(--border-color);
    border-radius: 6px;
    margin-bottom: 20px;
    font-size: 0.95rem;
}

.stats-bar {
    background-color: #06020c;
    padding: 15px;
    border-radius: 8px;
    margin-bottom: 15px;
}

.stat {
    margin-bottom: 10px;
}

.stat .label {
    display: inline-block;
    width: 110px;
    font-size: 0.85rem;
    color: var(--text-secondary);
}

.bar-bg {
    display: inline-block;
    width: calc(100% - 120px);
    background-color: #1a0b35;
    border-radius: 4px;
    height: 18px;
    vertical-align: middle;
}

.bar {
    background: linear-gradient(90deg, var(--neon-red), var(--neon-gold));
    height: 100%;
    border-radius: 4px;
    text-align: right;
    font-size: 0.75rem;
    line-height: 18px;
    font-weight: bold;
    color: #fff;
    padding-right: 5px;
    box-sizing: border-box;
}

.weapon-desc {
    font-size: 0.9rem;
    color: var(--text-secondary);
}

.spin-royale {
    background-color: #06020c;
    border: 1px solid var(--border-color);
    border-radius: 8px;
    padding: 15px;
    margin-top: 25px;
}

.spin-result {
    text-align: center;
    font-size: 1rem;
    color: #00ffcc;
    font-weight: bold;
    margin-top: 15px;
    text-shadow: 0 0 10px rgba(0, 255, 204, 0.4);
}

footer {
    text-align: center;
    padding: 30px;
    color: var(--text-secondary);
    font-size: 0.85rem;
    border-top: 1px solid var(--border-color);
    margin-top: 50px;
}
""".trimIndent()

        val scriptJs = """// Free Fire Companion Workspace App Code
let activeSquad = ["DJ Alok"];
const weapons = {
    mp40: {
        name: "MP40 - Special Ops",
        dmg: "48%",
        rof: "83%",
        rng: "22%",
        desc: "The ultimate close-range beast in Free Fire. Dominates with insane fire rate and speed!"
    },
    m1014: {
        name: "M1014 - Demolitionist",
        dmg: "94%",
        rof: "18%",
        rng: "14%",
        desc: "Close quarters shotgun. Delivers massive single-shot pellet damage that obliterates squads."
    },
    awm: {
        name: "AWM - Duke Swallowtail",
        dmg: "90%",
        rof: "11%",
        rng: "91%",
        desc: "High precision bolt-action sniper rifle. One headshot triggers immediate action-knockdown!"
    },
    woodpecker: {
        name: "Woodpecker - Crimson",
        dmg: "72%",
        rof: "35%",
        rng: "63%",
        desc: "Supreme marksman rifle featuring high armor penetration. Destroys protective vests easily."
    }
};

const spins = [
    "🔥 Grand Prize: Evolutionary MP40 Cobra Skin (Permanent)!",
    "💎 Diamond Royale Voucher x5!",
    "✨ Loot Box - Dragon Breath!",
    "🥈 Bounty Token Playcard (3D)",
    "🎖️ Kelly 'The Swift' Awakening Shard",
    "🎟️ Weapon Royale Ticket x3"
];

function selectChar(name) {
    // Select element active switching
    const cards = document.querySelectorAll('.char-card');
    cards.forEach(card => {
        if(card.innerText.includes(name)) {
            card.classList.add('active');
        } else {
            card.classList.remove('active');
        }
    });

    if(!activeSquad.includes(name)) {
        if(activeSquad.length >= 3) {
            activeSquad.shift();
        }
        activeSquad.push(name);
    }
    updateSquadSlots();
}

function updateSquadSlots() {
    for(let i=1; i<=3; i++) {
        const slotEl = document.getElementById("slot-" + i);
        if(activeSquad[i-1]) {
            slotEl.innerText = (i === 1 ? "🎵 " : (activeSquad[i-1] === "Wukong" ? "🍃 " : "🛡️ ")) + activeSquad[i-1] + (i===1 ? " (Leader)" : "");
        } else {
            slotEl.innerText = "Empty";
        }
    }
}

function simulateActiveBoost() {
    const log = document.getElementById("combat-log");
    log.innerText = "Synchronizing squad boosts... ";
    setTimeout(() => {
        log.innerText = "⚡ Synergy Active! DJ Alok restores " + (activeSquad.length * 5) + " HP/sec. Squad speed increased by 15%!";
    }, 400);
}

function updateWeaponGuide() {
    const sel = document.getElementById("weapon-select").value;
    const data = weapons[sel];
    if(data) {
        document.getElementById("w-name").innerText = data.name;
        document.getElementById("w-desc").innerText = data.desc;
        
        const dmgEl = document.getElementById("w-dmg");
        dmgEl.style.width = data.dmg;
        dmgEl.innerText = data.dmg;

        const rofEl = document.getElementById("w-rof");
        rofEl.style.width = data.rof;
        rofEl.innerText = data.rof;

        const rngEl = document.getElementById("w-rng");
        rngEl.style.width = data.rng;
        rngEl.innerText = data.rng;
    }
}

function spinRoyale() {
    const resultEl = document.getElementById("spin-result");
    resultEl.innerText = "🎯 Spinning V.O.I.D. lucky wheel...";
    let count = 0;
    const interval = setInterval(() => {
        resultEl.innerText = "🎲 " + spins[Math.floor(Math.random() * spins.length)];
        count++;
        if(count > 8) {
            clearInterval(interval);
            const winner = spins[Math.floor(Math.random() * spins.length)];
            resultEl.innerHTML = "<span style='color: #ff0055;'>🏆 CONGRATS!</span> " + winner;
        }
    }, 150);
}
""".trimIndent()

        return listOf(
            ProjectFile(workspaceId = workspaceId, path = "index.html", content = indexHtml),
            ProjectFile(workspaceId = workspaceId, path = "style.css", content = styleCss),
            ProjectFile(workspaceId = workspaceId, path = "script.js", content = scriptJs)
        )
    }

    fun generateMinecraftTemplateFiles(workspaceId: Long): List<ProjectFile> {
        val packUuidHeader = UUID.randomUUID().toString()
        val packUuidModule = UUID.randomUUID().toString()
        
        val manifestJson = """{
  "format_version": 2,
  "header": {
    "description": "V.O.I.D. Herobrine Boss Battle Addon pack containing behavior files to summon the legendary boss Herobrine.",
    "name": "V.O.I.D. Herobrine Boss Battle (Behavior)",
    "uuid": "$packUuidHeader",
    "version": [1, 0, 0],
    "min_engine_version": [1, 14, 0]
  },
  "modules": [
    {
      "description": "Herobrine Behaviors & Lightning mechanics",
      "type": "data",
      "uuid": "$packUuidModule",
      "version": [1, 0, 0]
    }
  ]
}
""".trimIndent()

        val herobrineJson = """{
  "format_version": "1.14.0",
  "minecraft:entity": {
    "description": {
      "identifier": "void:herobrine",
      "is_spawnable": true,
      "is_summonable": true,
      "is_experimental": false
    },
    "components": {
      "minecraft:type_family": {
        "family": [ "herobrine", "monster", "mob" ]
      },
      "minecraft:health": {
        "value": 300,
        "max": 300
      },
      "minecraft:boss": {
        "name": "Herobrine",
        "should_darken_sky": true,
        "hud_range": 30
      },
      "minecraft:damage_sensor": {
        "sensors": [
          {
            "on_damage": {
              "filters": { "test": "is_difficulty", "operator": "==", "value": "peaceful" },
              "event": "minecraft:despawn"
            }
          },
          {
            "on_damage": {
              "filters": { "test": "has_damage", "value": "fire" },
              "cause": "fire"
            },
            "damage_multiplier": 0.0
          }
        ]
      },
      "minecraft:movement": {
        "value": 0.35
      },
      "minecraft:navigation.walk": {
        "can_path_over_water": true,
        "can_path_over_lava": false
      },
      "minecraft:movement.basic": {},
      "minecraft:jump.static": {},
      "minecraft:behavior.nearest_attackable_target": {
        "priority": 1,
        "entity_types": [
          {
            "filters": { "test": "is_family", "subject": "other", "value": "player" },
            "max_dist": 40
          }
        ]
      },
      "minecraft:behavior.melee_attack": {
        "priority": 2,
        "speed_multiplier": 1.4,
        "track_target": true
      },
      "minecraft:attack": {
        "damage": 12
      },
      "minecraft:teleport": {
        "random_teleport_chance": 0.15,
        "max_random_teleport_range": 16.0
      },
      "minecraft:nameable": {
        "always_show": true,
        "default_name": "Herobrine"
      }
    }
  }
}
""".trimIndent()

        val resourceManifestJson = """{
  "format_version": 2,
  "header": {
    "description": "V.O.I.D. Herobrine Resource Pack - Custom glowing eyes client render support",
    "name": "V.O.O.I.D. Herobrine Resources",
    "uuid": "${UUID.randomUUID()}",
    "version": [1, 0, 0],
    "min_engine_version": [1, 14, 0]
  },
  "modules": [
    {
      "description": "Textures and model layout files for Herobrine Boss Summon",
      "type": "resources",
      "uuid": "${UUID.randomUUID()}",
      "version": [1, 0, 0]
    }
  ]
}
""".trimIndent()

        val herobrineClientJson = """{
  "format_version": "1.14.0",
  "minecraft:client_entity": {
    "description": {
      "identifier": "void:herobrine",
      "materials": {
        "default": "zombie_glowing"
      },
      "textures": {
        "default": "textures/entity/herobrine"
      },
      "geometry": {
        "default": "geometry.humanoid.custom"
      },
      "render_controllers": [ "controller.render.default" ]
    }
  }
}
""".trimIndent()

        val instructionsMd = """# V.O.I.D. Herobrine Battle Pack Starter

This Bedrock custom pack summons **Herobrine**—an legendary boss entity featuring high health (300 hp), physical melee combat attacks, aggressive threat tracking for nearby players, and sudden teleportation traits.

## How to Summon
In Minecraft (with behavior pack and resource pack synced):
1. In-game, open Chat Commands (`/`).
2. Run this command:
   `/summon void:herobrine`
3. The sky will darken instantly, and Herobrine’s custom boss hud will flash!

## Customization Instructions
Open file: `entities/herobrine.json` inside your physical Bedrock behavior directory in V.O.I.D.:
- To adjust total health pool, search `"minecraft:health"` and modify the `"value"` and `"max"` fields.
- To modify melee hit damage, adjust `"minecraft:attack"` `"damage"` value.
- To modify movement stats and triggers, customize `"speed_multiplier"` or edit `"minecraft:teleport"` parameters.

*Note: Dominate development with JJK Supremacy programmatically verified in V.O.I.D. terminal!*
""".trimIndent()

        return listOf(
            ProjectFile(workspaceId = workspaceId, path = "manifest.json", content = manifestJson),
            ProjectFile(workspaceId = workspaceId, path = "behavior_packs/herobrine_behavior/pack_manifest.json", content = manifestJson),
            ProjectFile(workspaceId = workspaceId, path = "behavior_packs/herobrine_behavior/entities/herobrine.json", content = herobrineJson),
            ProjectFile(workspaceId = workspaceId, path = "resource_packs/herobrine_resource/pack_manifest.json", content = resourceManifestJson),
            ProjectFile(workspaceId = workspaceId, path = "resource_packs/herobrine_resource/entity/herobrine.entity.json", content = herobrineClientJson),
            ProjectFile(workspaceId = workspaceId, path = "instructions.md", content = instructionsMd)
        )
    }
}

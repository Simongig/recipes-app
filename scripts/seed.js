db = db.getSiblingDB('kochbuch_dev');

db.Recipe.insertMany([
  {
    "_id": "6a9072178e874d71c3ef10b8",
    "title": "Vegane Spaghetti Bolognese",
    "duration": 40,
    "preparationSteps": [
      {
        "title": "Sojagranulat einweichen",
        "content": "Das Sojagranulat in eine hitzebeständige Schüssel geben und mit heißer Gemüsebrühe bedecken. Etwa 10 Minuten quellen lassen und dabei ab und zu umrühren."
      },
      {
        "title": "Gemüse vorbereiten",
        "content": "In der Zwischenzeit die Möhren schälen und grob raspeln. Zwiebel und Knoblauch schälen und fein würfeln."
      },
      {
        "title": "Sojagranulat ausdrücken",
        "content": "Das eingeweichte Sojagranulat in ein feines Sieb gießen und kräftig ausdrücken, bis möglichst wenig Flüssigkeit übrig ist."
      },
      {
        "title": "Sojagranulat anbraten",
        "content": "Öl in einer großen Pfanne erhitzen und das Sojagranulat bei hoher Hitze etwa 5 Minuten anbraten, bis es leicht gebräunt ist. Dann die Hitze reduzieren, Sojasoße, Tomatenmark und Agavendicksaft unterrühren und 2-3 Minuten weiterbraten. Möhren, Zwiebel und Knoblauch dazugeben und weitere 5 Minuten mitbraten."
      },
      {
        "title": "Soße köcheln lassen",
        "content": "Wer möchte, löscht jetzt mit Rotwein ab und lässt ihn einkochen. Stückige und passierte Tomaten dazugeben und die Soße bei kleiner Hitze mindestens 15 Minuten köcheln lassen - je länger, desto besser. Oregano und Basilikum einrühren und die Bolognese mit Agavendicksaft, Salz und Pfeffer abschmecken."
      },
      {
        "title": "Nudeln kochen und servieren",
        "content": "Reichlich Salzwasser aufkochen und die Spaghetti nach Packungsanleitung garen. Eine Kelle Nudelwasser in die Bolognese rühren. Die Spaghetti abgießen, mit der Soße vermengen und mit veganem Parmesan servieren."
      }
    ],
    "ingredients": [
      {
        "name": "Spaghetti",
        "normalizedKey": "spaghetti",
        "quantity": 500,
        "unit": "GRAM"
      },
      {
        "name": "Feines Sojagranulat",
        "normalizedKey": "sojagranulat",
        "quantity": 100,
        "unit": "GRAM"
      },
      {
        "name": "Heiße Gemüsebrühe zum Einweichen",
        "normalizedKey": "gemuesebruehe",
        "quantity": 400,
        "unit": "MILLILITER"
      },
      {
        "name": "Möhren",
        "normalizedKey": "moehre",
        "quantity": 3,
        "unit": "PIECE"
      },
      {
        "name": "Zwiebel",
        "normalizedKey": "zwiebel",
        "quantity": 1,
        "unit": "PIECE"
      },
      {
        "name": "Knoblauchzehe",
        "normalizedKey": "knoblauch",
        "quantity": 1,
        "unit": "CLOVE"
      },
      {
        "name": "Sojasoße",
        "normalizedKey": "sojasosse",
        "quantity": 1,
        "unit": "TABLESPOON"
      },
      {
        "name": "Tomatenmark",
        "normalizedKey": "tomatenmark",
        "quantity": 2,
        "unit": "TABLESPOON"
      },
      {
        "name": "Agavendicksaft",
        "normalizedKey": "agavendicksaft",
        "quantity": 2,
        "unit": "TABLESPOON"
      },
      {
        "name": "Veganer Rotwein (optional)",
        "normalizedKey": "rotwein",
        "quantity": 100,
        "unit": "MILLILITER"
      },
      {
        "name": "Stückige Tomaten",
        "normalizedKey": "stueckige_tomaten",
        "quantity": 400,
        "unit": "GRAM"
      },
      {
        "name": "Passierte Tomaten",
        "normalizedKey": "passierte_tomaten",
        "quantity": 400,
        "unit": "MILLILITER"
      },
      {
        "name": "Getrockneter Oregano",
        "normalizedKey": "oregano",
        "quantity": 0.5,
        "unit": "TABLESPOON"
      },
      {
        "name": "Getrocknetes Basilikum",
        "normalizedKey": "basilikum",
        "quantity": 0.5,
        "unit": "TABLESPOON"
      },
      {
        "name": "Salz",
        "normalizedKey": "salz",
        "quantity": 1,
        "unit": "PINCH"
      },
      {
        "name": "Pfeffer",
        "normalizedKey": "pfeffer",
        "quantity": 1,
        "unit": "PINCH"
      },
      {
        "name": "Neutrales Pflanzenöl",
        "normalizedKey": "pflanzenoel",
        "quantity": 2,
        "unit": "TABLESPOON"
      },
      {
        "name": "Veganer Parmesan zum Servieren",
        "normalizedKey": "veganer_parmesan",
        "quantity": 30,
        "unit": "GRAM"
      }
    ],
    "imagePaths": [
      "https://images.unsplash.com/photo-1692071097529-320eb2b32292?q=80&w=776&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
    ],
    "portions": 4,
    "visibility": "PRIVATE",
    "createdBy": "admin",
    "ownerId": ""
  },
  {
    "_id": "6a900fe78e874d71c3ef10b7",
    "title": "Vegane Pancakes mit Zitrone und pflanzlichem Joghurt",
    "duration": 30,
    "preparationSteps": [
      {
        "title": "Zitrone vorbereiten",
        "content": "Die Zitrone unter heißem Wasser abwaschen und gut abtrocknen. Die Schale fein abreiben und die Zitrone anschließend auspressen."
      },
      {
        "title": "Teig anrühren",
        "content": "Mehl, Backpulver, Zucker und Salz in einer Schüssel mischen. Pflanzliche Milch, Joghurt, den Zitronenabrieb und den Zitronensaft dazugeben und alles zu einem glatten Teig verrühren. Den Teig 10 Minuten ruhen lassen."
      },
      {
        "title": "Pancakes ausbacken",
        "content": "Etwas Öl in einer kleinen Pfanne bei schwacher Hitze erhitzen. Pro Pancake 2-3 EL Teig in die Pfanne geben. Sobald sich kleine Bläschen auf der Oberfläche bilden und der Rand fest wird, den Pancake wenden und die zweite Seite goldbraun backen. So nach und nach alle Pancakes ausbacken."
      },
      {
        "title": "Servieren",
        "content": "Die Pancakes stapeln und mit pflanzlichem Joghurt, frischen Beeren, Nüssen, Minzblättern und etwas Zitronenabrieb servieren."
      }
    ],
    "ingredients": [
      {
        "name": "Mehl",
        "normalizedKey": "mehl",
        "quantity": 300,
        "unit": "GRAM"
      },
      {
        "name": "Pflanzliche Milch",
        "normalizedKey": "pflanzliche_milch",
        "quantity": 150,
        "unit": "MILLILITER"
      },
      {
        "name": "Backpulver",
        "normalizedKey": "backpulver",
        "quantity": 15,
        "unit": "GRAM"
      },
      {
        "name": "Pflanzlicher Joghurt",
        "normalizedKey": "pflanzlicher_joghurt",
        "quantity": 180,
        "unit": "GRAM"
      },
      {
        "name": "Brauner Zucker",
        "normalizedKey": "brauner_zucker",
        "quantity": 4,
        "unit": "TABLESPOON"
      },
      {
        "name": "Zitrone",
        "normalizedKey": "zitrone",
        "quantity": 1,
        "unit": "PIECE"
      },
      {
        "name": "Salz",
        "normalizedKey": "salz",
        "quantity": 1,
        "unit": "PINCH"
      },
      {
        "name": "Pflanzliches Öl zum Braten",
        "normalizedKey": "pflanzliches_oel",
        "quantity": 2,
        "unit": "TABLESPOON"
      },
      {
        "name": "Frische Beeren",
        "normalizedKey": "beeren",
        "quantity": 1,
        "unit": "HANDFUL"
      },
      {
        "name": "Frische Minze",
        "normalizedKey": "minze",
        "quantity": 1,
        "unit": "SPRIG"
      },
      {
        "name": "Nüsse, z. B. Pekannüsse oder Haselnüsse",
        "normalizedKey": "nuesse",
        "quantity": 1,
        "unit": "HANDFUL"
      }
    ],
    "imagePaths": [
      "https://images.unsplash.com/photo-1528207776546-365bb710ee93?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
    ],
    "portions": 4
  },
  {
  "_id": "62caa3e80962ef746d0a6ea5",
  "duration": 20,
  "imagePaths": [
    "https://images.unsplash.com/photo-1707448460889-e268eb742820?q=80&w=884&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
  ],
  "ingredients": [
    {
      "name": "Rahmspinat",
      "normalizedKey": "rahmspinat",
      "quantity": 450,
      "unit": "GRAM"
    },
    {
      "name": "Sahneschmelzkäse",
      "normalizedKey": "sahneschmelzkaese",
      "quantity": 200,
      "unit": "GRAM"
    },
    {
      "name": "Sahne",
      "normalizedKey": "sahne",
      "quantity": 200,
      "unit": "MILLILITER"
    },
    {
      "name": "Brühe",
      "normalizedKey": "bruehe",
      "quantity": 0.25,
      "unit": "LITER"
    },
    {
      "name": "Spaghetti",
      "normalizedKey": "spaghetti",
      "quantity": 500,
      "unit": "GRAM"
    }
  ],
  "portions": 4,
  "preparationSteps": [
    {
      "title": "Spinatsoße kochen",
      "content": "Den Rahmspinat in einem Topf bei mittlerer Hitze auftauen lassen. Sahne und Brühe dazugießen und die Soße einige Minuten einköcheln lassen, bis sie sämig ist."
    },
    {
      "title": "Spaghetti kochen",
      "content": "Währenddessen reichlich Salzwasser aufkochen und die Spaghetti darin nach Packungsanleitung garen."
    },
    {
      "title": "Soße verfeinern",
      "content": "Zum Schluss den Sahneschmelzkäse in die Spinatsoße rühren, bis er vollständig geschmolzen ist."
    },
    {
      "title": "Servieren",
      "content": "Die Spaghetti abgießen und mit der Spinatsoße servieren. Wer es deftiger mag, brät zusätzlich Speck in einer Pfanne knusprig aus und reicht ihn dazu."
    }
  ],
  "title": "Spaghetti mit Spinatsoße"
  },
  {
  "_id": "6335d5e18104e07a0d39d92b",
  "duration": 60,
  "imagePaths": [
    "https://images.unsplash.com/photo-1574894709920-11b28e7367e3?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D\n"
  ],
  "ingredients": [
    {
      "name": "Käse",
      "normalizedKey": "käse",
      "quantity": 200,
      "unit": "GRAM"
    },
    {
      "name": "Karotte",
      "normalizedKey": "karotte",
      "quantity": 2,
      "unit": "PIECE"
    },
    {
      "name": "Zwiebel",
      "normalizedKey": "zwiebel",
      "quantity": 1,
      "unit": "PIECE"
    },
    {
      "name": "Öl",
      "normalizedKey": "Öl",
      "quantity": 2,
      "unit": "TABLESPOON"
    },
    {
      "name": "Knoblauch",
      "normalizedKey": "knoblauch",
      "quantity": 1,
      "unit": "PIECE"
    },
    {
      "name": "Hackfleisch",
      "normalizedKey": "hackfleisch",
      "quantity": 250,
      "unit": "GRAM"
    },
    {
      "name": "Wasser",
      "normalizedKey": "wasser",
      "quantity": 125,
      "unit": "MILLILITER"
    },
    {
      "name": "Gemüsebrühe",
      "normalizedKey": "gemüsebrühe",
      "quantity": 1,
      "unit": "TEASPOON"
    },
    {
      "name": "Zucker",
      "normalizedKey": "zucker",
      "quantity": 1,
      "unit": "TEASPOON"
    },
    {
      "name": "Salz",
      "normalizedKey": "salz",
      "quantity": 1,
      "unit": "PINCH"
    },
    {
      "name": "Italienische Kräuter",
      "normalizedKey": "italienische kräuter",
      "quantity": 2,
      "unit": "PINCH"
    },
    {
      "name": "Butter",
      "normalizedKey": "butter",
      "quantity": 2,
      "unit": "TABLESPOON"
    },
    {
      "name": "Milch",
      "normalizedKey": "milch",
      "quantity": 500,
      "unit": "MILLILITER"
    },
    {
      "name": "Mehl",
      "normalizedKey": "mehl",
      "quantity": 2,
      "unit": "TABLESPOON"
    },
    {
      "name": "Nudelplatten",
      "normalizedKey": "nudelplatten",
      "quantity": 200,
      "unit": "GRAM"
    },
    {
      "name": "Tomaten (gehackt)",
      "normalizedKey": "tomaten (gehackt)",
      "quantity": 250,
      "unit": "GRAM"
    }
  ],
  "portions": 4,
  "preparationSteps": [
    {
      "content": "- Auflaufform einfetten\n- Käse reiben\n- Karotten schälen und in Würfel schneiden\n- Zwiebel schälen und würfeln",
      "title": "Vorbereitung"
    },
    {
      "content": "- Öl in einer Pfanne erhitzen\n- Zwiebeln andünsten\n- Knoblauch schneiden\n- Hackfleisch zugeben und anbraten\n- Tomaten hinzugeben\n- Wasser aufgießen\n- Mit Gemüsebrühe, Zucker und Gewürzen abschmecken\n- 5-10 Minuten garen lassen\n",
      "title": "Hackfleisch-Soße"
    },
    {
      "content": "- Butter schmelzen\n- Mehl hinzugeben und sorgfältig mit einem Schneebesen verrühren\n- Milch aufgießen und aufkochen lassen\n- Mit Salz und Pfeffer abschmecken",
      "title": "Bechamel-Soße"
    },
    {
      "content": "- etwas Hackfleisch-Soße auf den Boden der Auflaufform verteilen\n- Danach kommt eine Schicht Bechamel-Soße\n- Danach wie folgt Schichten: Nudeln, Hackfleischsoße, Nudeln, Bechamel-Soße bis nur noch etwas Bechamelsoße übrig ist\n- Für die letzte Schicht auf die Schicht Nudelplatten Bechamel-Soße geben und mit Käse bedecken\n",
      "title": "Lasagne einschichten"
    },
    {
      "content": "Bei 160° Heißluft für 30-40 Minuten im Ofen garen bis der Käse goldbraun ist",
      "title": "Garen"
    }
  ],
  "title": "Lasagne"
  },
  {
  "_id": "63cd25e1a2ada115944e64d8",
  "duration": 15,
  "imagePaths": [
    "https://images.unsplash.com/photo-1546549032-9571cd6b27df?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80"
  ],
  "ingredients": [
    {
      "name": "Spaghetti",
      "normalizedKey": "spaghetti",
      "quantity": 300,
      "unit": "GRAM"
    },
    {
      "name": "Speck oder Pancetta",
      "normalizedKey": "speck oder pancetta",
      "quantity": 50,
      "unit": "GRAM"
    },
    {
      "name": "Ei",
      "normalizedKey": "ei",
      "quantity": 1,
      "unit": "PIECE"
    },
    {
      "name": "Parmesan",
      "normalizedKey": "parmesan",
      "quantity": 100,
      "unit": "GRAM"
    }
  ],
  "portions": 2,
  "preparationSteps": [
    {
      "content": "Spaghetti nach Packungsanweisung bissfest kochen. 1 Tasse des Nudelkochwassers zurückbehalten.",
      "title": "Spaghetti zubereiten"
    },
    {
      "content": "Eine große Pfanne erhitzen, den Speck oder Pancetta direkt bei geringer Hitze hinzugeben und knusprig braten. ",
      "title": "Speck anbraten"
    },
    {
      "content": "In einer separaten Schüssel Eier, geriebenen Parmesankäse und schwarzen Pfeffer verquirlen.",
      "title": "Zubereitung der Sauce"
    },
    {
      "content": "Spaghetti abgießen und in die Pfanne mit dem Speck geben. Schwenken, um sie zu vermengen. Die Pfanne vom Herd nehmen, die Eimischung dazugeben und schnell schwenken, damit die Nudeln gleichmäßig bedeckt sind. Wenn die Masse zu dick ist, können Sie etwas Nudelwasser hinzufügen, um sie cremiger zu machen. Sofort servieren.",
      "title": "Fertigstellung des Gerichts"
    }
  ],
  "title": "Spaghetti Carbonara"
},
{
  "_id": "63ffb4ffefe5ae7dd6862ef3",
  "duration": 30,
  "imagePaths": [
    "https://images.unsplash.com/photo-1481931098730-318b6f776db0?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwzMTc2MTV8MHwxfHNlYXJjaHwxfHxQYXN0YS1BZ2xpbyUyMGUlMjBPbGlvfGVufDB8fHx8MTY3NzcwMjM5OQ&ixlib=rb-4.0.3&q=80&w=1080"
  ],
  "ingredients": [
    {
      "name": "Spaghetti",
      "normalizedKey": "spaghetti",
      "quantity": 500,
      "unit": "GRAM"
    },
    {
      "name": "Ölivenöl",
      "normalizedKey": "Ölivenöl",
      "quantity": 50,
      "unit": "MILLILITER"
    },
    {
      "name": "Knoblauch",
      "normalizedKey": "knoblauch",
      "quantity": 4,
      "unit": "PIECE"
    },
    {
      "name": "Chiliflocken",
      "normalizedKey": "chiliflocken",
      "quantity": 3,
      "unit": "PINCH"
    },
    {
      "name": "Petersilie",
      "normalizedKey": "petersilie",
      "quantity": 10,
      "unit": "GRAM"
    }
  ],
  "portions": 4,
  "preparationSteps": [
    {
      "content": "Füllen Sie zunächst einen großen Topf mit Wasser und fügen Sie eine großzügige Menge Salz hinzu (ich verwende normalerweise etwa 2 Esslöffel Salz). Bringen Sie das Wasser zum Kochen und fügen Sie dann die Nudeln hinzu und kochen Sie sie gemäß den Anweisungen auf der Verpackung. Am besten kochen Sie die Nudeln al dente oder nur leicht bissfest, da Sie sie später in der Sauce verarbeiten werden.\n\nBevor Sie die Nudeln abgießen, sollten Sie etwa 1 ½ Tassen des stärkehaltigen Nudelwassers aufbewahren, das Sie für die Sauce benötigen.",
      "title": "Pasta kochen"
    },
    {
      "content": "Warten Sie nicht, bis die Nudeln fertig gekocht sind, bevor Sie mit der Sauce beginnen. Etwa 4 bis 5 Minuten, nachdem Sie die Nudeln in das kochende Wasser gegeben haben, können Sie mit der Zubereitung der Sauce beginnen.\n\nErhitzen Sie etwas natives Olivenöl extra bei mittlerer Hitze in einer Pfanne und geben Sie dann den Knoblauch hinzu. Kochen Sie, bis der Knoblauch gerade anfängt, golden zu werden (achten Sie darauf, dass er nicht braun wird), rühren Sie regelmäßig um und passen Sie die Hitze nach Bedarf an, um ein Überkochen zu vermeiden.\n\nEinige rote Paprikaflocken hinzugeben und weitere 30 Sekunden schwenken. Wenn die Nudeln noch nicht gar sind, die Pfanne vom Herd nehmen und warten, bis die Nudeln gar sind.",
      "title": "Sauce zubereiten"
    },
    {
      "content": "Mit einer Schöpfkelle etwas von dem reservierten Nudelkochwasser in die Pfanne geben. Bei mittlerer Hitze köcheln lassen, bis die Flüssigkeit um etwa ein Drittel reduziert ist.",
      "title": "Soße binden"
    },
    {
      "content": "Nachdem die Sauce eingekocht ist, die gekochten Nudeln in die Pfanne geben. Die Nudeln und die Soße einige Minuten lang bei niedriger Hitze schwenken. Dann den Herd ausschalten und etwas geriebenen Parmesankäse und gehackte Petersilie hinzufügen. Schwenken Sie alles, bis es gut vermischt ist.\n\nIch empfehle, die Nudeln vor dem Servieren ein paar Minuten ruhen zu lassen, damit sie die Sauce aufsaugen können.",
      "title": "Finalisieren"
    }
  ],
  "title": "Pasta Aglio e Olio"
}
]);

print("✓ Seeded 5 recipes into Recipe collection");

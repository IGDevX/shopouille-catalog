package org.shopouille.seeder;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.shopouille.entity.Category;
import org.shopouille.entity.Product;
import org.shopouille.entity.ProductCategory;
import org.shopouille.entity.ProductCategoryId;
import org.shopouille.entity.Variant;

/**
 * Seeder just execute when quarkus.data-seeder.enabled=true
 */
@ApplicationScoped
public class DataSeeder {

    private static final Logger LOGGER = Logger.getLogger("DataSeeder");

    @ConfigProperty(name = "app.data-seeder.enabled", defaultValue = "false")
    boolean seederEnabled;

    private final Random random = new Random();
    private final AtomicInteger skuCounter = new AtomicInteger(1000);

    private final String[] productNames = {
            "IPA Cascade",
            "Lager Pure Malt",
            "Stout Black Mountain",
            "Triple Abbaye de Lumière",
            "Blanche Citrus Wave",
            "Pilsner Gold Draft",
            "Session IPA Light Brew",
            "Amber Ale Red Harvest",
            "Porter Smoky Barrel",
            "Saison Farmhouse Original",
            "IPA Tropical Storm",
            "Lager Mountain Fresh",
            "Stout Midnight Roast",
            "Blonde Summer Breeze",
            "Pale Ale Hoppy Trail",
            "Porter Chocolate Ember",
            "Double IPA Thunder Hop",
            "Red Ale Rustic Flame",
            "Weissbier Cloudy Peak",
            "Brown Ale Caramel Roots",
            "Sour Berry Splash",
            "Gose Salty Shore",
            "Barleywine Royal Oak",
            "Lager Frosted Malt",
            "IPA Citrus Explosion",
            "Stout Vanilla Night",
            "Blanche Snow Drift",
            "Pilsner Noble Hop",
            "Tripel Golden Abbey",
            "Porter Dark River"
    };

    private final String[] categoryNames = {
            "Bières Artisanales",
            "IPA",
            "Lager",
            "Ales",
            "Blanches",
            "Brunes & Stouts",
            "Bières d’Abbaye",
            "Bières Saison",
            "Edition Limitée",
            "Pack Dégustation"
    };

    @Transactional
    public void seed(@Observes StartupEvent ev) {
        if (!seederEnabled) {
            LOGGER.info("Skip seeder (not dev)");
            return;
        }

        seedCategories();
        seedProductsVariantsAndCategories();
    }

    private void seedCategories() {
        if (Category.count() > 0) {
            LOGGER.info("Categories already seeded");
            return;
        }
        for (String name : categoryNames) {
            Category c = new Category();
            c.name = name;
            c.persist();
        }
        LOGGER.info("Categories seeded");
    }

    private void seedProductsVariantsAndCategories() {
        if (Product.count() > 0) {
            LOGGER.info("Products already seeded");
            return;
        }

        for (String name : productNames) {
            LOGGER.info("Create product: " + name);
            Product p = new Product();
            p.title = name;
            p.slug = toSlug(name);
            p.descriptionHtml = "Découvrez la bière artisanale " + name + ", brassée avec passion.";
            p.seoTitle = name + " | Shopouille";
            p.seoDescription = "Bière " + name + " — goût authentique.";
            p.publishedAt = Instant.now();
            p.persist();

            // pick 1..3 distinct categories for this product
            List<Category> pickedCategories = pickRandomCategories(1 + random.nextInt(3));

            // create 2..4 variants and attach the picked categories to each variant
            int variantsCount = 2 + random.nextInt(3);
            for (int i = 0; i < variantsCount; i++) {
                Variant v = new Variant();
                v.product = p;
                v.sku = generateSku(p.title);
                String volume = randomPick("33cl", "50cl", "75cl");
                String hop = randomPick("Cascade", "Citra", "Mosaic", "Saaz");
                String packaging = randomPick("Bouteille", "Canette");
                double alcohol = 4.0 + random.nextDouble() * 6.0;

                v.attributesJson = String.format(
                        "{\"volume\":\"%s\",\"packaging\":\"%s\",\"houblon\":\"%s\",\"alcool\":\"%.1f%%\"}",
                        escapeJson(volume), escapeJson(packaging), escapeJson(hop), alcohol);
                v.priceAmount = 250 + random.nextInt(750);
                v.quantity = 10 + random.nextInt(50);
                v.isActive = true;
                v.persist();

                for (Category c : pickedCategories) {
                    ProductCategory pc = new ProductCategory();
                    pc.id = new ProductCategoryId();
                    pc.variant = v;
                    pc.category = c;
                    pc.persist();
                }
            }
        }
        LOGGER.info("Products, variants and product_categories seeded");
    }

    private List<Category> pickRandomCategories(int count) {
        List<Category> result = new ArrayList<>();
        List<Integer> used = new ArrayList<>();
        while (result.size() < count) {
            int idx = random.nextInt(categoryNames.length);
            if (used.contains(idx))
                continue;
            used.add(idx);
            Category c = Category.find("name", categoryNames[idx]).firstResult();
            if (c != null)
                result.add(c);
        }
        return result;
    }

    private String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String randomPick(String... values) {
        return values[random.nextInt(values.length)];
    }

    private String generateSku(String productName) {
        return "SKU-" + skuCounter.getAndIncrement() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String toSlug(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return normalized.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+)|(-+$)", "");
    }
}

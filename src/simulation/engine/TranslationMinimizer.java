package simulation.engine;

/**
 * A type of maximizer that tries to minimize CollisionResults' translations.
 * 
 * @author Jacob Taylor
 * 
 */
public final class TranslationMinimizer extends Maximizer<CollisionResult> {
	@Override
	protected double measure(CollisionResult res) {
		return -res.translation().magnitude();
	}
}

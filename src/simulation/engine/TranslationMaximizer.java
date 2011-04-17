package simulation.engine;

/**
 * A type of maximizer that tries to maximize CollisionResults' translations.
 * 
 * @author Jacob Taylor
 * 
 */
public final class TranslationMaximizer extends Maximizer<CollisionResult> {
	@Override
	protected double measure(CollisionResult res) {
		return res.translation().magnitude();
	}
}

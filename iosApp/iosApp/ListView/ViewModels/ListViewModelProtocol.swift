/// Copyright (c) 2022 Razeware LLC
/// 
/// Permission is hereby granted, free of charge, to any person obtaining a copy
/// of this software and associated documentation files (the "Software"), to deal
/// in the Software without restriction, including without limitation the rights
/// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
/// copies of the Software, and to permit persons to whom the Software is
/// furnished to do so, subject to the following conditions:
/// 
/// The above copyright notice and this permission notice shall be included in
/// all copies or substantial portions of the Software.
/// 
/// Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
/// distribute, sublicense, create a derivative work, and/or sell copies of the
/// Software in any work that is designed, intended, or marketed for pedagogical or
/// instructional purposes related to programming, coding, application development,
/// or information technology.  Permission for such use, copying, modification,
/// merger, publication, distribution, sublicensing, creation of derivative works,
/// or sale is expressly withheld.
/// 
/// This project and source code may use libraries or frameworks that are
/// released under various Open-Source licenses. Use of those libraries and
/// frameworks are governed by their own individual licenses.
///
/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
/// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
/// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
/// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
/// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
/// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
/// THE SOFTWARE.

import shared
import UIKit
import Combine

protocol ListViewModelProtocol: ObservableObject {
  var dataSource: [ListRowViewModel] { get set }
	func rowTapped(model :ListRowViewModel)
}

class ListViewModel: ListViewModelProtocol {
  private unowned let coordinator: ListCoordinator
	private var disposables = Set<AnyCancellable>()

  @Published var dataSource: [ListRowViewModel] = []
  
	//swiftlint:disable implicitly_unwrapped_optional
	var api: GrainApi!
	//swiftlint:enable implicitly_unwrapped_optional
	
	var listSubject = CurrentValueSubject<[Grain], Never>([])

  init(coordinator: ListCoordinator) {
    self.coordinator = coordinator
		
		api = GrainApi(context: UIViewController()) // Shouldn't limit context to a VC!!!

		self.getList()
			.print("debug: +++")
			.map { grainList -> [ListRowViewModel] in
				grainList.map{ [weak self] in
					ListRowViewModel.init(grain:$0, api: self?.api)
				}
			}
			.assign(to: \.dataSource, on: self)
			.store(in: &disposables)
  }
	
	func getList() -> AnyPublisher<[Grain], Never> {
		api.getGrainList(success: { grains in
			self.listSubject.send(grains)
		}, failure: { error in
			print(error?.description() ?? "")
		})
		
		return self.listSubject.eraseToAnyPublisher()
	}
	
	func rowTapped(model :ListRowViewModel) {
		api.setFavorite(id: Int32(model.id), value: !model.isFav)
		//Not perfect here
		self.listSubject.send(self.listSubject.value)
	}
}


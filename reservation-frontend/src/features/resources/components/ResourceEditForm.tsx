import { useState } from 'react';
import type { ResourceInfo } from '../../types';
import { AddResourceModal } from './AddResourceModal';
import { ResourceEditModal } from './ResourceEditModal';
import { useDeleteResource } from '../hook/useDeleteResource';
import { useLogout } from '../../auth/hooks/useLogout';
import { getReservationsExistByResource } from '../api/getReservationsExistByResource';

interface Props {
  resources: ResourceInfo[];
  css: string | null;
}

export const ResourceEditForm = ({ resources, css }: Props) => {
  const [modal, setModal] = useState<{
    type: 'ADD' | 'EDIT' | null;
    id: number;
  }>({ type: null, id: -1 });
  const closeModal = () => setModal({ type: null, id: -1 });
  const deleteResource = useDeleteResource();
  const { logout } = useLogout();

  return (
    <div className="resourceEditForm">
      <style>{css}</style>
      <ul className="resource-list">
        {resources.map((r) => (
          <li className="resource-entity" key={r.id}>
            <h3>{r.name}</h3>
            <div className="btn-grp">
              <button
                className="edit-btn"
                disabled={r.id === -1}
                onClick={() => setModal({ type: 'EDIT', id: r.id ? r.id : -1 })}
              >
                수정
              </button>

              <button
                className="delete-btn"
                disabled={r.id === -1}
                onClick={async () => {
                  if (window.confirm('정말로 삭제하시겠습니까?')) {
                    const exist = await getReservationsExistByResource(
                      r.id ? r.id : -1,
                    );
                    console.log(exist.exist);
                    if (exist.exist) {
                      if (
                        window.confirm(
                          '예약이 존재합니다 그래도 삭제하시겠습니까?',
                        )
                      )
                        deleteResource.mutate(r.id ? r.id : -1);
                    } else {
                      deleteResource.mutate(r.id ? r.id : -1);
                    }
                  }
                }}
              >
                삭제
              </button>
            </div>
          </li>
        ))}
        <button
          className="post-modal-btn"
          onClick={() => setModal({ type: 'ADD', id: -1 })}
        >
          추가
        </button>
      </ul>

      <button className="logout" onClick={() => logout()}>
        로그아웃
      </button>

      {modal.type === 'ADD' && (
        <AddResourceModal isOpen={true} onClose={closeModal} />
      )}

      {modal.type === 'EDIT' && (
        <ResourceEditModal isOpen={true} id={modal.id} onClose={closeModal} />
      )}
    </div>
  );
};
